package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.debtposition.InstallmentOperationTypeResolver;
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
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IUVInstallmentsExportFlowFileDTOMapperTest {
    @Mock
    private InstallmentOperationTypeResolver installmentOperationTypeResolver;

    private IUVInstallmentsExportFlowFileDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUVInstallmentsExportFlowFileDTOMapper(installmentOperationTypeResolver);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(installmentOperationTypeResolver);
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

        Mockito.when(installmentOperationTypeResolver.calculateInstallmentOperationType(installmentDTO))
                .thenReturn(PaymentEventType.DPI_EXPIRED);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                mapper.map(installmentDTO));

        assertEquals("It's not possible to identify Action with paymentEventType: DPI_EXPIRED", ex.getMessage());
    }

    private void map(PaymentEventType paymentEventType, IUVInstallmentsExportFlowFileDTO.ActionEnum action) {
        InstallmentDTO installmentDTO = buildInstallmentDTO();

        Mockito.when(installmentOperationTypeResolver.calculateInstallmentOperationType(installmentDTO))
                .thenReturn(paymentEventType);

        IUVInstallmentsExportFlowFileDTO result = mapper.map(installmentDTO);

        checkNotNullFields(result, "debtPositionTypeCode", "paymentType");
        assertEquals(action, result.getAction());
    }
}
