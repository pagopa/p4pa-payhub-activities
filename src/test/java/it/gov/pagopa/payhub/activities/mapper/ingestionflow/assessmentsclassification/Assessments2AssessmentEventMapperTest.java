package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

@ExtendWith(MockitoExtension.class)
class Assessments2AssessmentEventMapperTest {

	private Assessments2AssessmentEventMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = new Assessments2AssessmentEventMapper();
	}

	private final PodamFactory podamFactory = TestUtils.getPodamFactory();

	@Test
	void mapThenOk() {
		Assessments assessmentsDTO = podamFactory.manufacturePojo(Assessments.class);
		AssessmentsDetail assessmentsDetailDTO = podamFactory.manufacturePojo(AssessmentsDetail.class);

		var result = mapper.map(assessmentsDTO, List.of(assessmentsDetailDTO));

		Assertions.assertNotNull(result);
		checkNotNullFields(result);
	}
}