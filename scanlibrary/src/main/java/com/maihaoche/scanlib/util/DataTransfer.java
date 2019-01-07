package com.maihaoche.scanlib.util;

/**
 * 数据转换工具类
 */
public class DataTransfer {
    /**
     * 将字节码转化为字符串
     * @param bs
     * @return
     */
    public static String xGetString(byte[] bs) {
        if (bs != null) {
            StringBuffer sBuffer = new StringBuffer();
            for (int i = 0; i < bs.length; i++) {
                sBuffer.append(String.format("%02x", bs[i]));
            }
            return sBuffer.toString();
        }
        return "null";
    }

    /**
     * 将字符串转化为字节码
     * @param string
     * @return
     */
    public static byte[] getBytesByHexString(String string) {
        string = string.replaceAll(" ", "");// delete spaces
        int len = string.length();
        if (len % 2 == 1) {
            return null;
        }
        byte[] ret = new byte[len / 2];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (byte) (Integer.valueOf(string.substring((i * 2), (i * 2 + 2)), 16) & 0xff);
        }
        return ret;
    }

}
