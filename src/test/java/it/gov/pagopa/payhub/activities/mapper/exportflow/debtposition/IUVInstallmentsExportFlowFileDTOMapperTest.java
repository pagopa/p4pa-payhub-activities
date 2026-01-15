package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Action;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionTypeOrgFaker.buildDebtPositionTypeOrgDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IUVInstallmentsExportFlowFileDTOMapperTest {

    private IUVInstallmentsExportFlowFileDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUVInstallmentsExportFlowFileDTOMapper();
    }

    @Test
    void givenActionIWhenMapThenOk() {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO, debtPositionTypeOrg);

        checkNotNullFields(result, "paymentType");
        assertEquals(Action.I, result.getAction());
    }
}
