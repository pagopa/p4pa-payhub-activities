package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryXlsIngestionFlowFileDTO {
    private String abiCode;
    private String cabCode;
    private String accountCode;
    private String currency;
    private LocalDate billDate;
    private LocalDate regionValueDate;
    private Long billAmountCents;
    private String sign;
    private String remittanceCode;
    private String checkNumber;
    private String bankReference;
    private String clientReference;
    private String remittanceDescription;
    private String extendedRemittanceDescription;
}
