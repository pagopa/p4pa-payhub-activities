package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessments;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail.AssessmentsDetailMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

@ExtendWith(MockitoExtension.class)
class AssessmentsMapperTest {

  private AssessmentsDetailMapper assessmentsDetailMapper;

  @BeforeEach
  void setUp() {
    String dataCipherPsw = "PSW";
    assessmentsDetailMapper = new AssessmentsDetailMapper(dataCipherPsw);
  }

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void mapThenOk() {
    AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
    Long organizationId = 123L;
    Long assessmentId = 123L;

    var result = assessmentsDetailMapper.map2AssessmentsDetailRequestBody(dto, organizationId, assessmentId);

    Assertions.assertNotNull(result);
    checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
        "updateTraceId", "links", "assessmentDetailId","paymentDateTime","receiptId");
  }

}