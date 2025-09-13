package org.jeecg.modules.jxcmanage.utils;

/*
 * ClassName: FileNameValidator
 * Package: org.jeecg.modules.jxcmanage.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/4 - 11:38
 * @Version: v1.0
 */

import java.util.regex.Pattern;

public class FileNameValidator {
    // 禁止出现的特殊字符正则（包含路径分隔符和系统不允许的字符）
    private static final Pattern INVALID_CHAR_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");

    // Windows系统保留文件名
    private static final String[] RESERVED_NAMES = {
            "con", "prn", "aux", "nul",
            "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
            "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"
    };

    /**
     * 校验文件名是否合法
     *
     * @param fileName 待校验的文件名
     * @return 合法返回true，否则返回false
     */
    public static boolean isValidFileName(String fileName) {
        // 空文件名校验
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        // 去除前后空格
        String trimmedName = fileName.trim();

        // 特殊字符校验
        if (INVALID_CHAR_PATTERN.matcher(trimmedName).find()) {
            return false;
        }

        // 路径遍历校验（包含..）
        if (trimmedName.contains("..")) {
            return false;
        }

        // 长度校验（通常不超过255字符）
        if (trimmedName.length() > 255) {
            return false;
        }

        // 系统保留名校验
        String baseName = getBaseName(trimmedName);
        for (String reserved : RESERVED_NAMES) {
            if (reserved.equalsIgnoreCase(baseName)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取文件名（不含扩展名）
     */
    public static String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    /**
     * 清理非法文件名（替换非法字符为下划线）
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown_file";
        }

        // 替换非法字符为下划线
        String sanitized = INVALID_CHAR_PATTERN.matcher(fileName).replaceAll("_");
        // 处理..
        sanitized = sanitized.replace("..", "__");
        // 截断过长文件名
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        // 处理系统保留名
        String baseName = getBaseName(sanitized);
        for (String reserved : RESERVED_NAMES) {
            if (reserved.equalsIgnoreCase(baseName)) {
                sanitized = sanitized + "_file";
                break;
            }
        }
        return sanitized;
    }
}

