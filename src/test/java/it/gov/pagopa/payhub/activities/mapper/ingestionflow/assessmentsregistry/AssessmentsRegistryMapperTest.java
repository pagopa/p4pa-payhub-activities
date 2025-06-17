package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistryStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryMapperTest {

  @InjectMocks
  private AssessmentsRegistryMapper assessmentsRegistryMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void mapThenOk() {
    AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
    dto.setStatus(AssessmentsRegistryStatus.ACTIVE.name());
    Long organizationIpaCode = 123L;

    var result = assessmentsRegistryMapper.map(dto, organizationIpaCode);

    Assertions.assertNotNull(result);
    checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
        "updateTraceId", "links");
  }

}