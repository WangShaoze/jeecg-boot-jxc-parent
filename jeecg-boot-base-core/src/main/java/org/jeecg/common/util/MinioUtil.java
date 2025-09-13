package org.jeecg.common.util;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.util.filter.SsrfFileTypeFilter;
import org.jeecg.common.util.filter.StrAttackFilter;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * minio文件上传工具类
 *
 * @author: jeecg-boot
 */
@Slf4j
public class MinioUtil {
    private static String minioUrl;
    private static String minioViewUrl;
    private static String minioName;
    private static String minioPass;
    private static String bucketName;

    public static void setMinioUrl(String minioUrl) {
        MinioUtil.minioUrl = minioUrl;
    }

    public static void setMinioViewUrl(String minioViewUrl) {
        MinioUtil.minioViewUrl = minioViewUrl;
    }

    public static void setMinioName(String minioName) {
        MinioUtil.minioName = minioName;
    }

    public static void setMinioPass(String minioPass) {
        MinioUtil.minioPass = minioPass;
    }

    public static void setBucketName(String bucketName) {
        MinioUtil.bucketName = bucketName;
    }

    public static String getMinioUrl() {
        return minioUrl;
    }

    public static String getMinioViewUrl() {
        return minioViewUrl;
    }

    public static String getBucketName() {
        return bucketName;
    }

    private static MinioClient minioClient = null;

    private static String getMimeTypeByFilename(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".pdf")) return "application/pdf";
        // fallback
        return "application/octet-stream";
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    public static String upload(MultipartFile file, String bizPath, String customBucket) throws Exception {
        String fileUrl = "";
        //update-begin-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
        bizPath = StrAttackFilter.filter(bizPath);
        //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击

        //update-begin-author:liusq date:20210809 for: 过滤上传文件类型
        SsrfFileTypeFilter.checkUploadFileType(file);
        //update-end-author:liusq date:20210809 for: 过滤上传文件类型

        String newBucket = bucketName;
        if (oConvertUtils.isNotEmpty(customBucket)) {
            newBucket = customBucket;
        }
        try {
            initMinio(minioUrl, minioName, minioPass);
            // 检查存储桶是否已经存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                log.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                /*minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());*/

                // 1. 创建桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());

                // 设置为完全公开（测试用）
                String policyJson = "{\n"
                        +
                        "    \"Version\": \"2012-10-17\",\n"
                        +
                        "    \"Statement\": [\n"
                        +
                        "        {\n"
                        +
                        "            \"Effect\": \"Allow\",\n"
                        +
                        "            \"Principal\": \"*\",\n"
                        +
                        "            \"Action\": [\n"
                        +
                        "                \"s3:GetObject\",\n"
                        +
                        "                \"s3:PutObject\",\n"
                        +
                        "                \"s3:DeleteObject\"\n"
                        +
                        "            ],\n"
                        +
                        "            \"Resource\": [\n"
                        +
                        "                \"arn:aws:s3:::" + newBucket + "/*\"\n"
                        +
                        "            ]\n"
                        +
                        "        }\n"
                        +
                        "    ]\n"
                        +
                        "}";

                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(newBucket)
                                .config(policyJson)
                                .build()
                );
                log.info("create a new bucket.");
            }
            InputStream stream = file.getInputStream();
            // 获取文件名
            String orgName = file.getOriginalFilename();
            if ("".equals(orgName)) {
                orgName = file.getName();
            }
            String contentType = getMimeTypeByFilename(orgName);
            orgName = CommonUtils.getFileName(orgName);
            String objectName = bizPath + "/"
                    + (orgName.indexOf(".") == -1
                    ? orgName + "_" + System.currentTimeMillis()
                    : orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."))
            );

            // 使用putObject上传一个本地文件到存储桶中。
            if (objectName.startsWith(SymbolConstant.SINGLE_SLASH)) {
                objectName = objectName.substring(1);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
                    .bucket(newBucket)
                    .contentType(contentType)
                    .stream(stream, stream.available(), -1).build();
            minioClient.putObject(objectArgs);
            stream.close();
            fileUrl = minioViewUrl + newBucket + SymbolConstant.SINGLE_SLASH+ objectName;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileUrl;
    }

    /**
     * 文件上传
     *
     * @param file
     * @param bizPath
     * @return
     */
    public static String upload(MultipartFile file, String bizPath) throws Exception {
        return upload(file, bizPath, null);
    }

    /**
     * 文件上传
     *
     * @param file     文件
     * @param fileName 文件名
     * @return
     */
    public static String upload(MultipartFile file, String fileName, String customBucket, String contentType) throws Exception {
        String fileUrl = "";
        SsrfFileTypeFilter.checkUploadFileType(file);
        //update-end-author:liusq date:20210809 for: 过滤上传文件类型

        String newBucket = bucketName;
        if (oConvertUtils.isNotEmpty(customBucket)) {
            newBucket = customBucket;
        }
        try {
            initMinio(minioUrl, minioName, minioPass);
            // 检查存储桶是否已经存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                log.info("Bucket already exists.");
            } else {
                // 1. 创建桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(newBucket)
                                .build()
                );
                log.info("create a new bucket.");
            }
            InputStream stream = file.getInputStream();

            // 使用putObject上传一个本地文件到存储桶中。
            if (fileName.startsWith(SymbolConstant.SINGLE_SLASH)) {
                fileName = fileName.substring(1);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().object(fileName)
                    .bucket(newBucket)
                    .contentType(contentType)
                    .stream(stream, stream.available(), -1).build();
            minioClient.putObject(objectArgs);
            stream.close();
            fileUrl = minioViewUrl + newBucket + SymbolConstant.SINGLE_SLASH + fileName;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileUrl;
    }


    /**
     * 获取文件流
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public static InputStream getMinioFile(String bucketName, String objectName) {
        InputStream inputStream = null;
        try {
            initMinio(minioUrl, minioName, minioPass);
            GetObjectArgs objectArgs = GetObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            inputStream = minioClient.getObject(objectArgs);
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     * @throws Exception
     */
    public static void removeObject(String bucketName, String objectName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            minioClient.removeObject(objectArgs);
        } catch (Exception e) {
            log.info("文件删除失败" + e.getMessage());
        }
    }

    /**
     * 获取文件外链
     *
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String getObjectUrl(String bucketName, String objectName, Integer expires) {
        initMinio(minioUrl, minioName, minioPass);
        try {
            //update-begin---author:liusq  Date:20220121  for：获取文件外链报错提示method不能为空，导致文件下载和预览失败----
            GetPresignedObjectUrlArgs objectArgs = GetPresignedObjectUrlArgs.builder().object(objectName)
                    .bucket(bucketName)
                    .expiry(expires).method(Method.GET).build();
            //update-begin---author:liusq  Date:20220121  for：获取文件外链报错提示method不能为空，导致文件下载和预览失败----
            String url = minioClient.getPresignedObjectUrl(objectArgs);
            return URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            log.info("文件路径获取失败" + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化客户端
     *
     * @param minioUrl
     * @param minioName
     * @param minioPass
     * @return
     */
    private static MinioClient initMinio(String minioUrl, String minioName, String minioPass) {
        if (minioClient == null) {
            try {
                minioClient = MinioClient.builder()
                        .endpoint(minioUrl)
                        .credentials(minioName, minioPass)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return minioClient;
    }

    /**
     * 上传文件到minio
     *
     * @param stream
     * @param relativePath
     * @return
     */
    public static String upload(InputStream stream, String relativePath) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Bucket already exists.");
        } else {
            // 创建一个名为ota的存储桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        }
        PutObjectArgs objectArgs = PutObjectArgs.builder().object(relativePath)
                .bucket(bucketName)
                .contentType("application/octet-stream")
                .stream(stream, stream.available(), -1).build();
        minioClient.putObject(objectArgs);
        stream.close();
        return minioViewUrl + bucketName + SymbolConstant.SINGLE_SLASH + relativePath;
    }


    /**
     * 上传文件到MinIO
     *
     * @param objectName  存储在MinIO中的对象名称
     * @param data        文件字节数组
     * @param contentType 文件类型
     * @throws Exception 可能抛出的异常
     */
    public static String upload(String objectName, byte[] data, String contentType) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        // 检查桶是否存在，不存在则创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        } else {
            log.info("Bucket already exists.");
        }

        // 使用putObject上传一个本地文件到存储桶中。
        if (objectName.startsWith(SymbolConstant.SINGLE_SLASH)) {
            objectName = objectName.substring(1);
        }

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .contentType(contentType)
                        .build()
        );
        return minioViewUrl + bucketName + SymbolConstant.SINGLE_SLASH + objectName;

    }

}
