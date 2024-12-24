package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;

import java.util.List;

public interface TreasuryValidatorService<T> {
    List<TreasuryErrorDTO> validateData(T fGC, String fileName);

    boolean validatePageSize(T fGC14, int sizeZipFile);
}