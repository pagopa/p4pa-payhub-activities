package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgMapperTest {

  @InjectMocks
  private DebtPositionTypeOrgMapper debtPositionTypeMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();


  @Test
  void givenDebtPositionTypeIngestionFlowFileWithPodamFactoryWhenMapThenOk() {
    DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
            DebtPositionTypeOrgIngestionFlowFileDTO.class);
    Long debtPositionTypeId = 123L;
    Long organizationId = 456L;

    var result = debtPositionTypeMapper.map(dto, debtPositionTypeId, organizationId);

    Assertions.assertNotNull(result);
    checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
        "updateTraceId", "debtPositionTypeOrgId","serviceId","notifyOutcomePushOrgSilServiceId","amountActualizationOrgSilServiceId");
    //todo : remove exclusion in checkNotNullFields for serviceId, notifyOutcomePushOrgSilServiceId e amountActualizationOrgSilServiceId with task https://pagopa.atlassian.net/browse/P4ADEV-3224
  }

}