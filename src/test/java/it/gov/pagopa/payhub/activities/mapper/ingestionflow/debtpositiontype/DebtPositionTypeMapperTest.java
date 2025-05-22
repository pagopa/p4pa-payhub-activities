package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeMapperTest {

  @InjectMocks
  private DebtPositionTypeMapper debtPositionTypeMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();


  @Test
  void givenDebtPositionTypeIngestionFlowFileWithPodamFactoryWhenMapThenOk() {
    DebtPositionTypeIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
        DebtPositionTypeIngestionFlowFileDTO.class);
    Long brokerId = 123L;

    var result = debtPositionTypeMapper.map(dto, brokerId);

    Assertions.assertNotNull(result);
    checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
        "updateTraceId", "debtPositionTypeId");
  }

}