package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * This interface provides a method for synchronize installment of a debt position on ACA service
 */
public interface AcaService {

    /**
     * Synchronize an installment of debt position on ACA
     *
     * @param iud the IUD of installment to be synchronized on ACA
     * @param debtPositionDTO the debt position related to the installment.
     */
    void syncInstallmentAca(String iud, DebtPositionDTO debtPositionDTO);

}
