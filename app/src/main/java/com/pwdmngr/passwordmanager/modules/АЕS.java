package com.pwdmngr.passwordmanager.modules;

import android.os.Build;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class АЕS {

    private static SecretKey key;

    public static String encrypt(String inputText) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException, KeyStoreException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Generate a random IV
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(inputText.getBytes("UTF-8"));

        // Base64 encode the IV and encrypted bytes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            byte[] combinedBytes = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combinedBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedBytes, ivBytes.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combinedBytes);
        }
        return null;
    }

    public static String decrypt(String inputText) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException, KeyStoreException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Decode the Base64 encoded input text
        byte[] combinedBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
        ) {
            combinedBytes = Base64.getDecoder().decode(inputText);
        }
        byte[] ivBytes = Arrays.copyOfRange(combinedBytes, 0, cipher.getBlockSize());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        // Initialize the cipher with the secret key and IV
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] encryptedBytes = Arrays.copyOfRange(combinedBytes, cipher.getBlockSize(), combinedBytes.length);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, "UTF-8");
    }

    public static void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        key = keygen.generateKey();
    }
}
