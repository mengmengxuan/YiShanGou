package com.personal.baseutils.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by 16323 on 2017/6/30.
 */

public class DESUtil {

//    private static byte[] iv = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
//    //-----------------加密-----------------
//    public static String encryptDES(String encryptString) throws Exception {
//
//     //    String encryptKey = "R@2o17rIwIsE";
//        String encryptKey = "R@2o17rIwIsE";
////        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
//        Cipher cipher = Cipher.getInstance("DES/EBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
//        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
//        return Base64DES.encode(encryptedData);
//    }
//    //------------------解密---------------------
//    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
//
//        byte[] byteMi = new Base64DES().decode(decryptString);
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);
////        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
//        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
//        byte decryptedData[] = cipher.doFinal(byteMi);
//        return new String(decryptedData);
//    }
//
//
//    private final static String HEX = "0123456789ABCDEF";
//    private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
//    private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
//    private final static String ALGORITHM = "DES";//DES是加密方式
//    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
//    /*
//    * 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
//  */
//    public static String generateKey() {
//        try {
//            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
//            byte[] bytes_key = new byte[20];
//            localSecureRandom.nextBytes(bytes_key);
//            String str_key = toHex(bytes_key);
//            return str_key;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    //二进制转字符
//    public static String toHex(byte[] buf) {
//        if (buf == null)
//            return "";
//        StringBuffer result = new StringBuffer(2 * buf.length);
//        for (int i = 0; i < buf.length; i++) {
//            appendHex(result, buf[i]);
//        }
//        return result.toString();
//    }
//
//    private static void appendHex(StringBuffer sb, byte b) {
//        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
//    }
//
//    public static Key getRawKey(String key) throws Exception {
//        DESKeySpec dks = new DESKeySpec(key.getBytes());
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
//        return keyFactory.generateSecret(dks);
//    }



    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey, zeroIv);
        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey, zeroIv);
        byte[] bytes = cipher.doFinal(data);
        return bytes;
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @return
     * @throws
     */
    public static String encrypt(String data) {
        byte[] bt = new byte[8];
        try {
            bt = encrypt(data.getBytes(), "R@2o17rIwIsE".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] strs = Base64.encode(bt, Base64.DEFAULT);
        String str = new String(strs);
        str = str.replace("\n", "");
        return str;
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @return
     * @throws
     */
    public static String decrypt(String data) {
        if (data == null)
            return null;
        String str = null;
        try {
            byte[] buf = Base64.decode(data.getBytes("utf8"), Base64.DEFAULT);
           byte[] key = "R@2o17rIwIsE".getBytes("utf8");
            byte[] bt = decrypt(buf, key);
            str = new String(bt, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
