package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import it.gov.pagopa.payhub.activities.util.EuroToCentsConverter;
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
    @CsvBindByName(column = "esercizio", required = true)
    private String billYear;

    @Size(max=7)
    @CsvBindByName(column = "n_provvisorio", required = true)
    private String billCode;

    @Size(max=10)
    @CsvBindByName(column = "data_esecuzione", required = true)
    private String billDate;

    @Size(max=255)
    @CsvBindByName(column = "anagrafica_cliente", required = true)
    private String pspLastName;

    @NotNull
    @Size(max=255)
    @CsvBindByName(column = "descrizione_causale", required = true)
    private String remittanceDescription;

    @CsvCustomBindByName(column = "importo_provvisorio", converter = EuroToCentsConverter.class, required = true)
    private Long billAmountCents;

    @CsvBindByName(column = "valuta_ente")
    private String regionValueDate;
}
