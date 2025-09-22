package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryCsvIngestionFlowFileDTO {

    @Pattern(regexp = "^\\d{4}$", message = "The year must be 4 digits long")
    @CsvBindByName(column = "ESERCIZIO", required = true)
    private String billYear;

    @Size(max=7)
    @CsvBindByName(column = "N.PROVV.", required = true)
    private String billCode;

    @Size(max=10)
    @CsvBindByName(column = "DATA ESEC.", required = true)
    private String billDate;

    @Size(max=255)
    @CsvBindByName(column = "ANAGRAFICA CLIENTE", required = true)
    private String pspLastName;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "DESCRIZIONE CAUSALE", required = true)
    private String remittanceDescription;

    @CsvBindByName(column = "IMPORTO PROVVISORIO", required = true)
    private String billAmount;

    @CsvBindByName(column = "VALUTA ENTE")
    private String regionValueDate;
}
