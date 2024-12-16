package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class AESUtilsTest {

    @Test
    void testStream() throws IOException {
        // Given
        String plain = "PLAINTEXT";
        String psw = "PSW";

        // When
        InputStream cipherStream = AESUtils.encrypt(psw, new ByteArrayInputStream(plain.getBytes(StandardCharsets.UTF_8)));
        InputStream resultStream = AESUtils.decrypt(psw, cipherStream);

        // Then
        Assertions.assertEquals(new String(resultStream.readAllBytes(), StandardCharsets.UTF_8), plain);
    }

    @Test
    void testFile() throws IOException {
        // Given
        String plain = "PLAINTEXT";
        Path plainFile = Path.of("build", "tmp", "plainFile.txt");
        Files.writeString(plainFile, plain);
        String psw = "PSW";
        Path decryptedFile = plainFile.getParent().resolve("decryptedFile.txt");

        // When
        File cipherFile = AESUtils.encrypt(psw, plainFile.toFile());
        AESUtils.decrypt(psw, cipherFile, decryptedFile.toFile());

        // Then
        Assertions.assertEquals(Files.readAllLines(decryptedFile), List.of(plain));
    }

    @Test
    void testEncryptString() {
        //Given
        String password = "password";
        String plainMessage = "plain message";

        //When
        byte[] encryptedMessage = AESUtils.encrypt(password, plainMessage);

        //Then
        Assertions.assertNotNull(encryptedMessage);
        Assertions.assertTrue(encryptedMessage.length > 0);
    }


    @Test
    void testDecryptString() {
        //Given
        String password = "password";
        String plainMessage = "plain message";

        //When
        byte[] encryptedMessage = AESUtils.encrypt(password, plainMessage);
        String decryptedMessage = AESUtils.decrypt(password, encryptedMessage);

        //Then
        Assertions.assertEquals(plainMessage, decryptedMessage);
    }


}
