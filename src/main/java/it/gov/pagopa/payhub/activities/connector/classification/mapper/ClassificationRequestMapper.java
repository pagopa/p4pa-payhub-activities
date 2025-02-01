package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassificationRequestMapper {
    ClassificationRequestBody map(Classification classification);
}