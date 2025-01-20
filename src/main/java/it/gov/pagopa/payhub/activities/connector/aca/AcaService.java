package it.gov.pagopa.payhub.activities.connector.aca;

import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;

/**
 * This interface provides a method for managing debt positions on ACA service
 */
public interface AcaService {

    /**
     * Create a debt position on ACA
     *
     * @param debtPositionDTO the debt position data to be created.
     */
    void createAcaDebtPosition(DebtPositionDTO debtPositionDTO);
}
