package com.finalsoft.helper;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptLib {


    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }


    private Cipher _cx;


    private byte[] _key, _iv;

    public CryptLib() throws NoSuchAlgorithmException, NoSuchPaddingException {

        _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _key = new byte[32]; //256 bit key space
        _iv = new byte[16]; //128 bit IV
    }



    private byte[] encryptDecrypt(String inputText, String encryptionKey,
                                  EncryptMode mode, String initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        int len = encryptionKey.getBytes("UTF-8").length;

        if (encryptionKey.getBytes("UTF-8").length > _key.length)
            len = _key.length;

        int ivlength = initVector.getBytes("UTF-8").length;

        if(initVector.getBytes("UTF-8").length > _iv.length)
            ivlength = _iv.length;

        System.arraycopy(encryptionKey.getBytes("UTF-8"), 0, _key, 0, len);
        System.arraycopy(initVector.getBytes("UTF-8"), 0, _iv, 0, ivlength);


        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(_iv);


        if (mode.equals(EncryptMode.ENCRYPT)) {

            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return _cx.doFinal(inputText.getBytes("UTF-8"));
        } else {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decodedValue = Base64.decode(inputText.getBytes(), Base64.DEFAULT);
            return _cx.doFinal(decodedValue);
        }
    }


    private static String SHA256 (String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String resultString;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes("UTF-8"));
        byte[] digest = md.digest();

        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02x", b)); //convert to hex
        }

        if(length > result.toString().length()) {
            resultString = result.toString();
        } else {
            resultString = result.toString().substring(0, length);
        }

        return resultString;

    }


    public String encryptPlainText(String plainText, String key, String iv) throws Exception {
        byte[] bytes = encryptDecrypt(plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, iv);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String decryptCipherText(String cipherText, String key, String iv) throws Exception {
        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, iv);
        return new String(bytes);
    }


    public String encryptPlainTextWithRandomIV(String plainText, String key) throws Exception {
        byte[] bytes = encryptDecrypt(generateRandomIV16() + plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, generateRandomIV16());
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String decryptCipherTextWithRandomIV(String cipherText, String key) throws Exception {
        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, generateRandomIV16());
        String out = new String(bytes);
        return out.substring(16, out.length());
    }




    public String generateRandomIV16() {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuilder result = new StringBuilder();
        for (byte b : aesKey) {
            result.append(String.format("%02x", b));
        }
        if (16 > result.toString().length()) {
            return result.toString();
        } else {
            return result.toString().substring(0, 16);
        }
    }

}