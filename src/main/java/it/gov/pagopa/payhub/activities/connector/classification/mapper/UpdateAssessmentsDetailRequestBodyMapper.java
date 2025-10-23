package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateAssessmentsDetailRequestBodyMapper {

	AssessmentsDetailRequestBody mapFromAssessmentsDetail(AssessmentsDetail ad);

}