package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * This interface provides a method for synchronize installment of a debt position on GPD service
 */
public interface GpdService {

    /**
     * Synchronize an installment of debt position on GPD
     *
     * @param iud the IUD of installment to be synchronized on GPD
     * @param debtPositionDTO the debt position related to the installment.
     */
    String syncInstallmentGpd(String iud, DebtPositionDTO debtPositionDTO);

}
