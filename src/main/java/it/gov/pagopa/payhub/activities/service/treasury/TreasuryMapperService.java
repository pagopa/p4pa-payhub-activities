package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;

public interface TreasuryMapperService<T, U> {

    U apply(T input, IngestionFlowFileDTO ingestionFlowFileDTO);
}