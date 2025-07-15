package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgMapperTest {

  @Mock
  private OrgSilServiceService orgSilServiceServiceMock;

  @InjectMocks
  private DebtPositionTypeOrgMapper debtPositionTypeMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();


  @Test
  void givenDebtPositionTypeIngestionFlowFileWithPodamFactoryWhenMapThenOk() {
    DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
            DebtPositionTypeOrgIngestionFlowFileDTO.class);
    Long debtPositionTypeId = 123L;
    Long organizationId = 456L;

    OrgSilService orgSilServicePaidNotificationOutcome = new OrgSilService();
    orgSilServicePaidNotificationOutcome.setOrgSilServiceId(111L);
    orgSilServicePaidNotificationOutcome.setApplicationName(dto.getNotifyOutcomePushOrgSilServiceCode());
    OrgSilService orgSilServiceActualization = new OrgSilService();
    orgSilServiceActualization.setOrgSilServiceId(222L);
    orgSilServiceActualization.setApplicationName(dto.getAmountActualizationOrgSilServiceCode());
    when(orgSilServiceServiceMock.getAllByOrganizationIdAndServiceType(organizationId, OrgSilServiceType.PAID_NOTIFICATION_OUTCOME))
        .thenReturn(Collections.singletonList(orgSilServicePaidNotificationOutcome));
    when(orgSilServiceServiceMock.getAllByOrganizationIdAndServiceType(organizationId, OrgSilServiceType.ACTUALIZATION))
        .thenReturn(Collections.singletonList(orgSilServiceActualization));

    var result = debtPositionTypeMapper.map(dto, debtPositionTypeId, organizationId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(111L, result.getNotifyOutcomePushOrgSilServiceId());
    Assertions.assertEquals(222L, result.getAmountActualizationOrgSilServiceId());
    checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
        "updateTraceId", "debtPositionTypeOrgId");
  }

}