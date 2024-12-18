package it.gov.pagopa.payhub.activities.service.cipher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HashAlgorithmTest {

    private HashAlgorithm hashAlgorithm;
    private byte[] pepper;

    @BeforeEach
    public void setup() {
        // Given
        pepper = Base64.getDecoder().decode("cGVwcGVyQWxwaGE="); // "pepperAlpha" in Base64
        hashAlgorithm = new HashAlgorithm("SHA-256", pepper);
    }

    @Test
    public void apply_ShouldReturnHash_GivenValidStringAndPepper() {
        // Given
        String input = "testString";

        // When
        byte[] hashResult = hashAlgorithm.apply(input);

        // Then
        assertThat(hashResult).isNotNull();
        assertThat(hashResult.length).isEqualTo(32); // SHA-256 produce 32 byte di hash
    }

    @Test
    public void apply_ShouldReturnDifferentHash_GivenDifferentStrings() {
        // Given
        String input1 = "testString1";
        String input2 = "testString2";

        // When
        byte[] hash1 = hashAlgorithm.apply(input1);
        byte[] hash2 = hashAlgorithm.apply(input2);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    public void apply_ShouldReturnSameHash_GivenSameInputAndPepper() {
        // Given
        String input = "sameInput";

        // When
        byte[] hash1 = hashAlgorithm.apply(input);
        byte[] hash2 = hashAlgorithm.apply(input);

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    public void apply_ShouldIncludePepperInHashCalculation() {
        // Given
        String input = "testString";
        byte[] pepperWithDifferentValue = Base64.getDecoder().decode("cGVwcGVyQmV0YQ=="); // "pepperBeta" in Base64
        HashAlgorithm hashAlgorithmWithDifferentPepper = new HashAlgorithm("SHA-256", pepperWithDifferentValue);

        // When
        byte[] hash1 = hashAlgorithm.apply(input);
        byte[] hash2 = hashAlgorithmWithDifferentPepper.apply(input);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    public void apply_ShouldNotThrowException_GivenEmptyString() {
        // Given
        String emptyInput = "";

        // When
        byte[] hashResult = hashAlgorithm.apply(emptyInput);

        // Then
        assertThat(hashResult).isNotNull();
        assertThat(hashResult.length).isEqualTo(32);
    }

    @Test
    public void apply_ShouldThrowException_GivenNullInput() {
        // Given
        String nullInput = null;

        // When & Then
        assertThatThrownBy(() -> hashAlgorithm.apply(nullInput))
                .isInstanceOf(NullPointerException.class);
    }
}
