package it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.organizationsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceMapperTest {

  @InjectMocks
  private OrgSilServiceMapper orgSilServiceMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();


  @Test
  void givenOrgSilServiceIngestionFlowFileWithPodamFactoryWhenMapThenOk() {
    OrgSilServiceIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
        OrgSilServiceIngestionFlowFileDTO.class);
    Long organizationId = 123L;

    var result = orgSilServiceMapper.map(dto, organizationId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(dto.getApplicationName(), result.getApplicationName());
    Assertions.assertEquals(dto.getServiceUrl(), result.getServiceUrl());
    Assertions.assertEquals(dto.getFlagLegacy(), result.getFlagLegacy());
  }
}
