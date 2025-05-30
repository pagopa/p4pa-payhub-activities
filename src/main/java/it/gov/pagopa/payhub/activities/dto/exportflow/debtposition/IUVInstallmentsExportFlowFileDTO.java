package it.gov.pagopa.payhub.activities.dto.exportflow.debtposition;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import it.gov.pagopa.pu.debtposition.dto.generated.EntityTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IUVInstallmentsExportFlowFileDTO {

    @CsvBindByName(column = "IUD")
    private String iud;

    @CsvBindByName(column = "codIUV")
    private String iuv;

    @CsvBindByName(column = "tipoIdentificativoUnivoco")
    private EntityTypeEnum entityType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco")
    private String fiscalCode;

    @CsvBindByName(column = "anagraficaPagatore")
    private String fullName;

    @CsvBindByName(column = "indirizzoPagatore")
    private String address;

    @CsvBindByName(column = "civicoPagatore")
    private String civic;

    @CsvBindByName(column = "capPagatore")
    private String postalCode;

    @CsvBindByName(column = "localitaPagatore")
    private String location;

    @CsvBindByName(column = "provinciaPagatore")
    private String province;

    @CsvBindByName(column = "nazionePagatore")
    private String nation;

    @CsvBindByName(column = "emailPagatore")
    private String email;

    @CsvBindByName(column = "dataEsecuzionePagamento")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    @CsvBindByName(column = "importoDovuto")
    private BigDecimal amount;

    @CsvBindByName(column = "tipoDovuto") 
    private String debtPositionTypeCode;

    @CsvBindByName(column = "causaleVersamento")
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione")
    private String legacyPaymentMetadata;

    @CsvBindByName(column = "bilancio")
    private String balance;

    @CsvBindByName(column = "flgGeneraIuv")
    private Boolean flagPuPagoPaPayment;

    @CsvBindByName(column = "commissioneCaricoPa")
    private Long paCommissionCents;

    @CsvBindByName(column = "azione")
    private ActionEnum action;

    public enum ActionEnum {
        I, M, A
    }
}
