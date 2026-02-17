package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DebtPositionTypeOrgIngestionFlowFileDTO {

    @CsvBindByName(column = "enteIpaCode", required = true)
    private String ipaCode;

    @CsvBindByName(column = "bilancioDefault")
    private String balance;

    @CsvBindByName(column = "codice", required = true)
    private String code;

    @CsvBindByName(column = "descrizione", required = true)
    private String description;

    @CsvBindByName(column = "codIban", required = true)
    private String iban;

    @CsvBindByName(column = "ibanPostale")
    private String postalIban;

    @CsvBindByName(column = "codiceContoPostale")
    private String postalAccountCode;

    @CsvBindByName(column = "intestatarioContoPostale")
    private String holderPostalCc;

    @CsvBindByName(column = "settoreOrganizzazione")
    private String orgSector;

    @CsvBindByName(column = "codicePagamentoSpontaneo")
    private String spontaneousFormCode;

    @CsvBindByName(column = "strutturaPagamentoSpontaneo")
    private String spontaneousFormStructure;

    @CsvBindByName(column = "importoCentesimi")
    private Long amountCents;

    @CsvBindByName(column = "urlPagamentoEsterno")
    private String externalPaymentUrl;

    @CsvBindByName(column = "codiceFiscaleAnonimo", required = true)
    private boolean flagAnonymousFiscalCode;

    @CsvBindByName(column = "scadenzaObbligatoria", required = true)
    private boolean flagMandatoryDueDate;

    @CsvBindByName(column = "pagamentoSpontaneo", required = true)
    private boolean flagSpontaneous;

    @CsvBindByName(column = "notificaIo", required = true)
    private boolean flagNotifyIo;

    @CsvBindByName(column = "templateMessaggioIo")
    private String ioTemplateMessage;

    @CsvBindByName(column = "attivo", required = true)
    private boolean flagActive;

    @CsvBindByName(column = "notificaEsitoPush", required = true)
    private boolean flagNotifyOutcomePush;

    @CsvBindByName(column = "codServNotificaEsitoPush")
    private String notifyOutcomePushOrgSilServiceCode;

    @CsvBindByName(column = "attualizzazioneImporto", required = true)
    private boolean flagAmountActualization;

    @CsvBindByName(column = "codServAttualizzazioneImporto")
    private String amountActualizationOrgSilServiceCode;

    @CsvBindByName(column = "esterno", required = true)
    private boolean flagExternal;

    @CsvBindByName(column = "codiceServizio")
    private String serviceCode;

    @CsvBindByName(column = "templateOggettoIo")
    private String ioTemplateSubject;

}
