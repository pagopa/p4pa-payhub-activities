package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.AssessmentsDetailFaker;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentsDetailRequestBodyMapperTest {

	private final UpdateAssessmentsDetailRequestBodyMapper mapper = Mappers.getMapper(UpdateAssessmentsDetailRequestBodyMapper.class);

	@Test
	void mapFromAssessmentsDetail() {
		//Given
		AssessmentsDetail assessmentsDetail = AssessmentsDetailFaker.buildAssessmentsDetail();
		//When
		AssessmentsDetailRequestBody actualResponse = mapper.mapFromAssessmentsDetail(assessmentsDetail);
		//Then
		TestUtils.reflectionEqualsByName(assessmentsDetail, actualResponse);
		TestUtils.checkNotNullFields(actualResponse);
	}

	@Test
	void mapFromNullAssessmentsDetail() {
		//Given
		AssessmentsDetail assessmentsDetail = null;
		//When
		AssessmentsDetailRequestBody actualResponse = mapper.mapFromAssessmentsDetail(assessmentsDetail);
		//Then
		Assertions.assertNull(actualResponse);
	}

}