package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Action;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IUVInstallmentsExportFlowFileDTOMapperTest {
    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    private IUVInstallmentsExportFlowFileDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUVInstallmentsExportFlowFileDTOMapper(debtPositionTypeOrgService);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(debtPositionTypeOrgService);
    }

    @Test
    void givenActionIWhenMapThenOk() {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        Mockito.when(debtPositionTypeOrgService.getById(1L))
                .thenReturn(debtPositionTypeOrg);

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO, 1L);

        checkNotNullFields(result, "debtPositionTypeCode", "paymentType");
        assertEquals(Action.I, result.getAction());
    }
}
