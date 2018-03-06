package com.suapp.dcdownloader.utils;

import android.content.Context;
import android.util.Base64;


import com.suapp.dcdownloader.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wangzhen@jiandaola.com on 23/12/2016.
 */

public class CipherUtils {

    private static native byte[] getAESKeyNative(Context context);

    public static byte[] getAESKey(Context context) {
        NativeLibraryLoader.loadLibrarySafely(context, "dailycast");
        try {
            return getAESKeyNative(context);
        } catch (UnsatisfiedLinkError error) {
            error.printStackTrace();
            return new byte[0];
        }
    }

//    public static String getPlayPublicKey(Context context) {
//        try {
//            String encryptKey = context.getString(R.string.play_public_key);
//            return new String(CipherUtils.decrypt(Base64.decode(encryptKey, Base64.NO_WRAP),
//                    CipherUtils.getAESKey(context)));
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public static byte[] encrypt(String sSrc, byte[] raw)
            throws GeneralSecurityException {
        return encrypt(sSrc.getBytes(), raw);
    }

    public static byte[] encrypt(byte[] sSrc, byte[] raw)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        return cipher.doFinal(sSrc);
    }

    public static void encrypt(InputStream input, OutputStream output, byte[] raw)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        CipherOutputStream cipherOutput = new CipherOutputStream(output, cipher);
        try {
            IOUtils.copy(input, cipherOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(input);
            IOUtils.close(cipherOutput);
        }
    }

    public static void decrypt(InputStream input, OutputStream output, byte[] raw)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

        CipherInputStream cipherInput = new CipherInputStream(input, cipher);
        try {
            IOUtils.copy(cipherInput, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(cipherInput);
            IOUtils.close(output);
        }
    }

    public static byte[] decrypt(byte[] encrypted, byte[] raw)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        return cipher.doFinal(encrypted);
    }
}
