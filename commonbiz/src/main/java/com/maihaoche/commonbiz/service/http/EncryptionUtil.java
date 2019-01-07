package com.maihaoche.commonbiz.service.http;

import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具（参考卖好车的加密配置）note:有漏洞，产生随机字符串的算法不安全。需要使用java自带的安全的伪随机算法。
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */
public class EncryptionUtil {

    /**
     * 获取随机的aesKey
     */
    public static String getAesKey() {
        //TODO 确保16位
        //生成随机16位aes——key
        String time = String.valueOf(System.currentTimeMillis());
        String aesKey = time + randomString(10);
        return aesKey.substring(0, 16);
    }

    private static PublicKey getPublicKey() {

        try {
            byte[] buffer = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 产生一个随机的字符串
     */
    public static String randomString(int length) {
        // FIXME: 17/6/6 这里产生随机字符的算法不安全。需要使用伪随机算法！
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 用rsa加密方式将aesKey加密成token
     */
    public static String getToken(String aesKey) {
        PublicKey publicKey = getPublicKey();


        if (publicKey == null) {
            return null;
        }
        Cipher cipher;
        try {
            // 使用默认RSA
            Security.addProvider(new BouncyCastleProvider());
            cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(aesKey.getBytes());
            return new String(Base64.encode(output, Base64.DEFAULT));
        } catch (Exception e) {
            return null;
        }
    }


    public static String aesEncrypt(String sSrc, String aesKey) {
        if (aesKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (aesKey.length() != 16) {
            return null;
        }
        byte[] raw = aesKey.getBytes();
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            return null;
        }


    }

    //    公司配置的公钥
    private static final String RSA = "RSA";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnjkOoOFbqvkzAw3aiPN2Jd7GO9U9IGYGlxtw3\n" +
            "lXySqnY2XcHicGbnxO09JrrP9EnW1psLRmHfboy2tFJjUaFlNQUEvjE/JPXctLPhClivOZOyKg31\n" +
            "7IPaObhgk1mgIjr3y/waUS4Kl9MmRPLkkrmdkKsGnLadf+nY6fHg1JZcNwIDAQAB";
}
