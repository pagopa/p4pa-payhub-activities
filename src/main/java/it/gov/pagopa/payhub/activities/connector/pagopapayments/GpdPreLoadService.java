package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * This interface provides a method for synchronize installment of a debt position on GPD PreLoad service
 */
public interface GpdPreLoadService {

    /**
     * Synchronize an installment of debt position on GPD PreLoad
     *
     * @param iud the IUD of installment to be synchronized on GPD PreLoad
     * @param debtPositionDTO the debt position related to the installment.
     */
    void syncInstallmentGpdPreLoad(String iud, DebtPositionDTO debtPositionDTO);

}
