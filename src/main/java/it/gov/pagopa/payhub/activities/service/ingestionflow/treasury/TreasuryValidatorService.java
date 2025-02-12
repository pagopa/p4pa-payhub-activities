package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;

import java.util.List;

public interface TreasuryValidatorService<T> {
    List<TreasuryErrorDTO> validateData(T fGC, String fileName);

    boolean validatePageSize(T fGC, int sizeZipFile);
}