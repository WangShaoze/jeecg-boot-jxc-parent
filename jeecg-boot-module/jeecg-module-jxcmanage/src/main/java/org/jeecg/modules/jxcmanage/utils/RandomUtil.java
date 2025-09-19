package org.jeecg.modules.jxcmanage.utils;

/*
 * ClassName: RandomUtil
 * Package: org.jeecg.modules.jxcmanage.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 18:23
 * @Version: v1.0
 */

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class RandomUtil {

    /**
     * 安全级别随机源，可复用
     */
    private static final SecureRandom SR = new SecureRandom();

    /**
     * 字符池：0-9 A-Z a-z
     */
    private static final String CHAR_POOL =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 使用线程安全的Set存储已生成的数字，确保唯一性
    private static final Set<Long> generatedNumbers = Collections.synchronizedSet(new HashSet<>());
    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成19位不重复的数字
     * @return 19位数字的字符串表示
     */
    public static String generate19DigitNumber() {
        long number;
        // 循环直到生成一个未出现过的19位数字
        do {
            // 19位数字范围：10^18 到 (10^19 - 1)
            // 先生成10^18到(10^19-1)之间的随机数
            long min = (long) Math.pow(10, 18);
            long max = (long) Math.pow(10, 19) - 1;

            // 生成范围内的随机数
            number = min + (long)(random.nextDouble() * (max - min + 1));
        } while (!generatedNumbers.add(number)); // 如果添加失败说明已存在，继续循环

        return String.valueOf(number);
    }

    /**
     * 生成 6 位大小写字母+数字随机串
     */
    public static String randomAlphanumeric6() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHAR_POOL.charAt(SR.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    /* ---------------- 示例调用 ---------------- */
    public static void main(String[] args) {
        System.out.println(randomAlphanumeric6()); // 每次输出如：k4Zq9B
        System.out.println(generate19DigitNumber());
    }
}
