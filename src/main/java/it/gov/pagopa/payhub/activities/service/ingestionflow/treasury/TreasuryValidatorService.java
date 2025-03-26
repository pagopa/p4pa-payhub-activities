package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorFileDTO;

import java.util.List;

public interface TreasuryValidatorService<T> {
    List<TreasuryErrorFileDTO> validateData(T fGC, String fileName);

    boolean validatePageSize(T fGC, int sizeZipFile);
}