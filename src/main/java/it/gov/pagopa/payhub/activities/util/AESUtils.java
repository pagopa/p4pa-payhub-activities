package it.gov.pagopa.payhub.activities.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESUtils {
    private AESUtils() {
    }

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final String ALGORITHM_TYPE = "AES";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    public static final String CIPHER_EXTENSION = ".cipher";

    public static byte[] getRandomNonce(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static SecretKey getSecretKey(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_INSTANCE);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM_TYPE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot initialize cryptographic data", e);
        }
    }

    public static InputStream encrypt(String password, InputStream plainStream) {
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        SecretKey secretKey = getSecretKey(password, salt);

        // GCM recommends 12 bytes iv
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        // prefix IV and Salt to cipher text
        byte[] prefix = ByteBuffer.allocate(iv.length + salt.length)
                .put(iv)
                .put(salt)
                .array();

        return new SequenceInputStream(
                new ByteArrayInputStream(prefix),
                new CipherInputStream(new BufferedInputStream(plainStream), cipher));
    }

    public static File encrypt(String password, File plainFile) {
        File cipherFile = new File(plainFile.getAbsolutePath() + CIPHER_EXTENSION);
        try(FileInputStream fis = new FileInputStream(plainFile);
            InputStream cipherStream = encrypt(password, fis)){
            Files.copy(cipherStream, cipherFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong when ciphering input file " + plainFile.getAbsolutePath(), e);
        }
        return cipherFile;
    }

    public static InputStream decrypt(String password, InputStream cipherStream) {
        try {
            byte[] iv = cipherStream.readNBytes(IV_LENGTH_BYTE);
            byte[] salt = cipherStream.readNBytes(SALT_LENGTH_BYTE);

            SecretKey secretKey = getSecretKey(password, salt);
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

            return new CipherInputStream(new BufferedInputStream(cipherStream), cipher);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read AES prefix data", e);
        }
    }

    public static void decrypt(String password, File cipherFile, File outputPlainFile) {
        try(FileInputStream fis = new FileInputStream(cipherFile);
            InputStream plainStream = decrypt(password, fis)){
            Files.copy(plainStream, outputPlainFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong when deciphering input file " + cipherFile.getAbsolutePath(), e);
        }
    }

    private static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException("Cannot initialize cipher data", e);
        }
    }

}
