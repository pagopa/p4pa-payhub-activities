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

/**
 * Utility class for AES encryption and decryption using the GCM mode.
 * Supports secure handling of files and data streams.
 */
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

    /**
     * Generates a random byte array to be used as a nonce.
     *
     * @param length the length of the nonce to generate.
     * @return a random byte array of the specified length.
     */
    public static byte[] getRandomNonce(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    /**
     * Derives an AES key from a password and a cryptographic salt using PBKDF2.
     *
     * @param password the password used for key derivation.
     * @param salt the cryptographic salt.
     * @return a derived AES key.
     * @throws IllegalStateException if the key derivation fails.
     */
    public static SecretKey getSecretKey(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_INSTANCE);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM_TYPE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot initialize cryptographic data", e);
        }
    }

    /**
     * Encrypts data from an input stream using AES GCM mode.
     *
     * @param password the password for encryption.
     * @param plainStream the input stream containing plaintext data.
     * @return an input stream containing encrypted data with the salt and IV prefixed.
     * @throws IllegalStateException if encryption fails.
     */
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

    /**
     * Encrypts a file using AES GCM mode.
     *
     * @param password the password for encryption.
     * @param plainFile the file to encrypt.
     * @return a file containing the encrypted data with the salt and IV prefixed.
     * @throws IllegalStateException if file encryption fails.
     */
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

    /**
     * Decrypts an encrypted input stream using AES GCM mode.
     *
     * @param password the password for decryption.
     * @param cipherStream the input stream containing encrypted data.
     * @return an input stream containing the decrypted data.
     * @throws IllegalStateException if decryption fails.
     */
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

    /**
     * Decrypts an encrypted file using AES GCM mode.
     *
     * @param password the password for decryption.
     * @param cipherFile the file containing encrypted data.
     * @param outputPlainFile the file to save the decrypted data.
     * @throws IllegalStateException if file decryption fails.
     */
    public static void decrypt(String password, File cipherFile, File outputPlainFile) {
        try(FileInputStream fis = new FileInputStream(cipherFile);
            InputStream plainStream = decrypt(password, fis)){
            Files.copy(plainStream, outputPlainFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong when deciphering input file " + cipherFile.getAbsolutePath(), e);
        }
    }

    /**
     * Initializes a Cipher instance with the specified mode, secret key, and IV.
     *
     * @param mode the cipher mode (Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
     * @param secretKey the secret key.
     * @param iv the initialization vector.
     * @return an initialized Cipher instance.
     * @throws IllegalStateException if cipher initialization fails.
     */
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
