package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
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

    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;

    @BeforeEach
    void setUp(){
        installmentSynchronizeMapperMock = new InstallmentSynchronizeMapper();
    }

    @Test
    void givenMapThenOk(){
        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(buildInstallmentIngestionFlowFileDTO(), 1L, 1L, 1L);

        assertEquals(4, Objects.requireNonNull(result.getAdditionalTransfers()).size());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenTransferMapNullThenThrowException() {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setTransfer2(null);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L)
        );

        assertEquals("Missing or empty transfer map for index: 2", exception.getMessage());
    }


    @Test
    void givenMapWhenFlagMultiBeneficiaryFalseThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setFlagMultibeneficiary(false);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setFlagMultiBeneficiary(false);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenNumberBeneficiaryNullThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(null);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(null);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result, "numberBeneficiary");
    }

    @Test
    void givenMapWhenNumberBeneficiaryLessThan2ThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(1);
        expected.setAdditionalTransfers(Collections.emptyList());

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(1);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L);

        reflectionEqualsByName(expected, result);
        assertTrue(Objects.requireNonNull(result.getAdditionalTransfers()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenRemittanceInformationFieldIsEmptyThenThrowException() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildTransferFake();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L)
        );

        assertEquals("Missing required value for keys: causaleVersamentoEnte or remittanceInformation", exception.getMessage());
    }
}
