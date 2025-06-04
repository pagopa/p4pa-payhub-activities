package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.debtposition.InstallmentOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
    private InstallmentOperationTypeResolver installmentOperationTypeResolver;
    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    private IUVInstallmentsExportFlowFileDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUVInstallmentsExportFlowFileDTOMapper(installmentOperationTypeResolver, debtPositionTypeOrgService);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(installmentOperationTypeResolver, debtPositionTypeOrgService);
    }

    @Test
    void givenActionIWhenMapThenOk() {
        map(PaymentEventType.DP_CREATED, IUVInstallmentsExportFlowFileDTO.ActionEnum.I);
    }

    @Test
    void givenActionMWhenMapThenOk() {
        map(PaymentEventType.DP_UPDATED, IUVInstallmentsExportFlowFileDTO.ActionEnum.M);
    }

    @Test
    void givenActionAWhenMapThenOk() {
        map(PaymentEventType.DP_CANCELLED, IUVInstallmentsExportFlowFileDTO.ActionEnum.A);
    }

    @Test
    void givenNotIdentifiedActionWhenMapThenThrowIllegalArgumentException() {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        Mockito.when(installmentOperationTypeResolver.calculateInstallmentOperationType(installmentDTO))
                .thenReturn(PaymentEventType.DPI_EXPIRED);

        Mockito.when(debtPositionTypeOrgService.getById(1L))
                .thenReturn(debtPositionTypeOrg);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                mapper.map(installmentDTO, 1L));

        assertEquals("It's not possible to identify Action with paymentEventType: DPI_EXPIRED", ex.getMessage());
    }

    private void map(PaymentEventType paymentEventType, IUVInstallmentsExportFlowFileDTO.ActionEnum action) {
        InstallmentDTO installmentDTO = buildInstallmentDTO();
        DebtPositionTypeOrg debtPositionTypeOrg = buildDebtPositionTypeOrgDTO();

        Mockito.when(installmentOperationTypeResolver.calculateInstallmentOperationType(installmentDTO))
                .thenReturn(paymentEventType);

        Mockito.when(debtPositionTypeOrgService.getById(1L))
                .thenReturn(debtPositionTypeOrg);

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO, 1L);

        checkNotNullFields(result, "debtPositionTypeCode", "paymentType");
        assertEquals(action, result.getAction());
    }
}
