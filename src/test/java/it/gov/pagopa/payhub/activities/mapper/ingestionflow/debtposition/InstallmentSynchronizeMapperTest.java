package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.TestUtils.reflectionEqualsByName;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildTransferFake;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InstallmentSynchronizeMapperTest {

    private InstallmentSynchronizeMapper installmentSynchronizeMapper;
    private final String FILENAME = "fileName.zip";

    @BeforeEach
    void setUp(){
        installmentSynchronizeMapper = new InstallmentSynchronizeMapper(new ObjectMapper());
    }

    @Test
    void givenMapThenOk() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setExecutionConfig(null);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        assertEquals(4, Objects.requireNonNull(result.getAdditionalTransfers()).size());
        assertEquals(NullNode.instance, result.getExecutionConfig());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenTransferMapNullThenThrowException() {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setTransfer2(null);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME)
        );

        assertEquals("Missing or empty transfer map for index: 2", exception.getMessage());
    }


    @Test
    void givenMapWhenFlagMultiBeneficiaryFalseThenOk() {
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setFlagMultibeneficiary(false);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setFlagMultiBeneficiary(false);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenNumberBeneficiaryNullThenOk() {
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(null);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(null);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result, "numberBeneficiary");
    }

    @Test
    void givenMapWhenNumberBeneficiaryLessThan2ThenOk() {
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(1);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(1);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenRemittanceInformationFieldIsEmptyThenThrowException() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildTransferFake();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME)
        );

        assertEquals("Missing required value for keys: causaleVersamentoEnte or remittanceInformation", exception.getMessage());
    }

    @Test
    void givenMapWhenExecutionConfigInvalidThenThrowException() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setExecutionConfig("{\"executionConfig\":\"test");

        InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME)
        );

        assertEquals(String.format("Invalid execution config value: [%s] ", installmentIngestionFlowFileDTO.getExecutionConfig()), exception.getMessage());
    }
}
