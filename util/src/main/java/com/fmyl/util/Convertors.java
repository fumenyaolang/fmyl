package com.fmyl.util;

import java.math.BigDecimal;

/**
 * 类型转换
 *
 * @author fmyl
 * @date 2018/7/5 下午3:30
 */
public class Convertors {

    /**
     * 把字符串转换成为int
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return: int
     * @Author: fmyl
     * @Date: 2018-07-05 15:07:24
     */
    public static int str2Int(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把字符串转换成为long
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return: long
     * @Author: fmyl
     * @Date: 2018-07-05 15:07:24
     */
    public static long str2Long(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把字符串转换成为double
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return: double
     * @Author: fmyl
     * @Date: 2018-07-05 15:07:24
     */
    public static double str2Double(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把字符串转换成为BigDecimal
     *
     * @param value        字符串
     * @param defaultValue 默认值
     * @return: BigDecimal
     * @Author: fmyl
     * @Date: 2018-07-05 15:07:24
     */
    public static BigDecimal str2BigDecimal(String value, BigDecimal defaultValue) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
