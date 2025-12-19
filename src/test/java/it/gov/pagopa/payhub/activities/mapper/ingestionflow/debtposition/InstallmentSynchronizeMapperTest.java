package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.NullNode;

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
    private static final String FILENAME = "fileName.zip";

    @BeforeEach
    void setUp(){
        installmentSynchronizeMapper = new InstallmentSynchronizeMapper(new JsonMapper());
    }

    @Test
    void givenMapThenOk() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setExecutionConfig(null);
        installmentIngestionFlowFileDTO.setTransfer1(null);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        assertEquals(4, Objects.requireNonNull(result.getAdditionalTransfers()).size());
        assertEquals(NullNode.instance, result.getExecutionConfig());
        checkNotNullFields(result);
    }

    @Test
    void givenMapUsingSecondarioThenOk() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        installmentIngestionFlowFileDTO.setExecutionConfig(null);
        MultiValuedMap<String, String> secondario = new ArrayListValuedHashMap<>();
        installmentIngestionFlowFileDTO.getTransfer2().entries().forEach(e -> secondario.put(e.getKey(), e.getValue()));
        installmentIngestionFlowFileDTO.setTransfer2(secondario);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L, FILENAME);

        assertEquals(5, Objects.requireNonNull(result.getAdditionalTransfers()).size());
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
        installmentIngestionFlowFileDTO.setTransfer1(null);
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
        installmentIngestionFlowFileDTO.setTransfer1(null);
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
        installmentIngestionFlowFileDTO.setTransfer1(null);
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

        assertEquals("Missing required value for keys: [causaleVersamentoEnte, remittanceInformation]", exception.getMessage());
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

    @Test
    void givenTransfer1WithSomeNullValuesThenShouldNotAddToAdditionalTransfers() {
        InstallmentIngestionFlowFileDTO dto = buildInstallmentIngestionFlowFileDTO();

        MultiValuedMap<String, String> transfer1 = new ArrayListValuedHashMap<>();
        transfer1.put("codiceFiscaleEnte_1", "codiceFiscaleEnte");
        transfer1.put("denominazioneEnte_1", null);
        transfer1.put("ibanAccreditoEnte_1", "ibanAccreditoEnte");
        transfer1.put("causaleVersamentoEnte_1", "causaleVersamentoEnte1");
        transfer1.put("importoVersamentoEnte_1", "1");
        transfer1.put("codiceTassonomiaEnte_1", "codiceTassonomiaEnte");

        dto.setTransfer1(transfer1);

        InstallmentSynchronizeDTO result = installmentSynchronizeMapper.map(dto, 1L, 1L, 1L, FILENAME);

        assertTrue(result.getAdditionalTransfers().stream().noneMatch(t -> "causaleVersamentoEnte1".equals(t.getRemittanceInformation())));
    }

}
