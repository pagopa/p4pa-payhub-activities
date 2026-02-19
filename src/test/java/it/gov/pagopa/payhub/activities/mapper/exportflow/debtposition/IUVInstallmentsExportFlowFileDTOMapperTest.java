package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Action;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildTransferDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Stream;

class IUVInstallmentsExportFlowFileDTOMapperTest {

    private IUVInstallmentsExportFlowFileDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUVInstallmentsExportFlowFileDTOMapper();
    }

    @Test
    void givenActionIWhenMapThenOk() {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        TransferDTO transferDTO2 = buildTransferDTO();
        transferDTO2.setTransferIndex(2);
        installmentDTO.getTransfers().add(transferDTO2);
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO, debtPositionTypeOrg);

        checkNotNullFields(result, "paymentType", "paCommission", "orgAddress2", "orgCivic2",
                "orgCap2", "orgLocation2", "orgProvince2", "orgNation2");
        assertEquals(Action.I, result.getAction());
    }

    @ParameterizedTest
    @MethodSource("provideRemittanceCases")
    void givenRemittanceCases_whenMap_thenExpected(String remittance, String originalRemittance, String expectedResult) {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        installmentDTO.setRemittanceInformation(remittance);
        installmentDTO.setOriginalRemittanceInformation(originalRemittance);
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO, debtPositionTypeOrg);

        assertEquals(expectedResult, result.getRemittanceInformation());
    }

    private static Stream<Arguments> provideRemittanceCases() {
        return Stream.of(
                Arguments.of("Standard remittance", null, "Standard remittance"),
                Arguments.of("Generated remittance", "Original remittance", "Original remittance")
        );
    }
}
