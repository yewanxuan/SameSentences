//package com.mobile.utils;
//import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

    private static MessageDigest _mdInst = null;
    private static char hexDigits[] = {'8', '9', '7', '4', '5', '0', '2', '6', '3', '1', 'C', 'D', 'A', 'B', 'E', 'F'};
    private static char hexDigits2[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest getMdInst() {
        if (_mdInst == null) {
            try {
                _mdInst = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return _mdInst;
    }

    /***
     * MD5加密 生成32位md5码
     * @param inStr 待加密字符串
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr) {
        try {
            byte[] btInput = inStr.getBytes("utf-8");
            // 使用指定的字节更新摘要
            getMdInst().update(btInput);
            // 获得密文
            byte[] md = getMdInst().digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits2[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits2[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int byteToInt(String str, int offset) {
        offset = offset % 28;
        byte[] byteArray = str.getBytes();
        int value = byteArray[0 + offset];
        value |= byteArray[1 + offset] << 8;
        value |= byteArray[2 + offset] << 16;
        value |= byteArray[3 + offset] << 24;
        return value;
    }

    /**
     * 测试主函数
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String str = "程序默认没有bug";
        System.out.println("原始：" + str);
        System.out.println("MD5(1)后：" + Md5.md5Encode(str));

    }
}