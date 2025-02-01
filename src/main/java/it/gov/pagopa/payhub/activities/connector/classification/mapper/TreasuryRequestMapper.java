package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TreasuryRequestMapper {
    TreasuryRequestBody map(Treasury treasury);
}
