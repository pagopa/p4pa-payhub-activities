package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;

public class TreasuryFaker {

    public static TreasuryDTO buildTreasuryDTO(){
        return TreasuryDTO.builder()
                .codIdUnivocoFlusso("FLOW_IDENTIFIER_CODE")
                .mygovFlussoTesoreriaId(1L)
                .build();
    }
}
