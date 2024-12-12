package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;

import java.math.BigDecimal;

public class TreasuryFaker {
    public static TreasuryDTO buildTreasuryDTO(){
        return TreasuryDTO.builder()
                .codIdUnivocoFlusso("IUF")
                .codBolletta("CODBOLLETTA")
                .numIpBolletta(new BigDecimal(1234))
                .build();
    }
}
