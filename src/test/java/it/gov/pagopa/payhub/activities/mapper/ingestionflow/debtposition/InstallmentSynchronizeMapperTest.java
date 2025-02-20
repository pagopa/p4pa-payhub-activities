package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.TestUtils.reflectionEqualsByName;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildTransferFake;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InstallmentSynchronizeMapperTest {

    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;

    @BeforeEach
    void setUp(){
        installmentSynchronizeMapperMock = new InstallmentSynchronizeMapper();
    }

    @Test
    void givenMapThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(buildInstallmentIngestionFlowFileDTO(), 1L, 1L);

        reflectionEqualsByName(expected, result, "transfersList");
        assertEquals(2, Objects.requireNonNull(result.getTransfersList()).size());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenFlagMultiBeneficiaryFalseThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setFlagMultibeneficiary(false);

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setFlagMultiBeneficiary(false);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L);

        reflectionEqualsByName(expected, result, "transfersList");
            assertTrue(Objects.requireNonNull(result.getTransfersList()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenNumberBeneficiaryNullThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(null);

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(null);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L);

        reflectionEqualsByName(expected, result, "transfersList");
        assertTrue(Objects.requireNonNull(result.getTransfersList()).isEmpty());
        checkNotNullFields(result, "numberBeneficiary");
    }

    @Test
    void givenMapWhenNumberBeneficiaryLessThan2ThenOk(){
        InstallmentSynchronizeDTO expected = buildInstallmentSynchronizeDTO();
        expected.setNumberBeneficiary(1L);

        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setNumberBeneficiary(1);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L);

        reflectionEqualsByName(expected, result, "transfersList");
        assertTrue(Objects.requireNonNull(result.getTransfersList()).isEmpty());
        checkNotNullFields(result);
    }

    @Test
    void givenMapWhenNoSuchMethodExceptionThenReturnTransferFieldNull(){
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildTransferFake();

        InstallmentSynchronizeDTO result = installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L);

        checkNotNullFields(Objects.requireNonNull(result.getTransfersList()).getFirst(), "orgFiscalCode");
    }
}
