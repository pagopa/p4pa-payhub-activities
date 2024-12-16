package it.gov.pagopa.payhub.activities.service.cipher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class DataCipherServiceTest {

    private DataCipherService dataCipherService;
    private ObjectMapper objectMapper;
    private final static String ENCRYPT_PASSWORD = "Test123!";
    private final static String HASH_PEPPER = Base64.getEncoder().encodeToString("TestPepper".getBytes(StandardCharsets.UTF_8));
    private final static PersonalData PERSONAL_DATA = new PersonalData("John", 30);
    private final static PersonalData INVALID_PERSONAL_DATA = new PersonalData("Invalid", 0);
    private final static  String SERIALIZED_OBJ = "{\"name\":\"John\",\"age\":30}";


    @BeforeEach
    public void setup() {
        objectMapper = mock(ObjectMapper.class);
        dataCipherService = new DataCipherService(ENCRYPT_PASSWORD, HASH_PEPPER, objectMapper);
    }

    @Test
    public void encrypt_ShouldEncryptPlainText_GivenValidInput() {
        // Given
        String plainText = "Plain text for test";

        // When
        byte[] encryptedData = dataCipherService.encrypt(plainText);

        // Then
        assertThat(encryptedData).isNotNull();
        String decryptedText = AESUtils.decrypt(ENCRYPT_PASSWORD, encryptedData);
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    public void decrypt_ShouldReturnPlainText_GivenValidEncryptedData() {
        // Given
        String plainText = "Plain text for test";
        byte[] encryptedData = AESUtils.encrypt(ENCRYPT_PASSWORD, plainText);

        // When
        String decryptedText = dataCipherService.decrypt(encryptedData);

        // Then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    public void encryptObj_ShouldReturnEncryptedData_GivenObject() throws JsonProcessingException {
        // Given
        Mockito.when(objectMapper.writeValueAsString(PERSONAL_DATA)).thenReturn(SERIALIZED_OBJ);

        // When
        byte[] encryptedData = dataCipherService.encryptObj(PERSONAL_DATA);

        // Then
        assertThat(encryptedData).isNotNull();
        String decryptedText = AESUtils.decrypt(ENCRYPT_PASSWORD, encryptedData);
        assertThat(decryptedText).isEqualTo(SERIALIZED_OBJ);
    }

    @Test
    public void decryptObj_ShouldReturnObject_GivenValidEncryptedData() throws IOException {
        // Given
        byte[] encryptedData = AESUtils.encrypt(ENCRYPT_PASSWORD, SERIALIZED_OBJ);

        Mockito.when(objectMapper.readValue(SERIALIZED_OBJ, PersonalData.class)).thenReturn(PERSONAL_DATA);

        // When
        PersonalData actualObject = dataCipherService.decryptObj(encryptedData, PersonalData.class);

        // Then
        assertThat(actualObject).isEqualTo(PERSONAL_DATA);
    }

    @Test
    public void encryptObj_ShouldThrowException_GivenInvalidObject() throws JsonProcessingException {
        // Given
        Mockito.when(objectMapper.writeValueAsString(INVALID_PERSONAL_DATA)).thenThrow(new JsonProcessingException("Cannot serialize") {});

        // When & Then
        assertThatThrownBy(() -> dataCipherService.encryptObj(INVALID_PERSONAL_DATA))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot serialize object as JSON");
    }


    @Test
    public void hash_ShouldReturnHashedValue_GivenValidInput() {
        // Given
        String input = "testString";

        // When
        byte[] hashedValue = dataCipherService.hash(input);

        // Then
        assertThat(hashedValue).isNotNull();
        assertThat(hashedValue.length).isGreaterThan(0);
    }

    @Test
    public void hash_ShouldReturnEmptyArray_GivenNullInput() {
        // Given
        String input = null;

        // When
        byte[] hashedValue = dataCipherService.hash(input);

        // Then
        assertThat(hashedValue).isEmpty();
    }

    // Helper class for testing objects
    static class PersonalData {
        private String name;
        private int age;

        public PersonalData() {
        }

        public PersonalData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PersonalData that = (PersonalData) o;
            return age == that.age && name.equals(that.name);
        }
    }
}
