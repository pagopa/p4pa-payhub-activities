package it.gov.pagopa.payhub.activities.dto.exportflow.debtposition;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptsArchivingExportFlowFileDTO {

    @CsvBindByName(column = "xml_rt")
    private String receiptXml;
    @CsvBindByName(column = "data_ora_messaggio_ricevuta")
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDateTime;
    @CsvBindByName(column = "id_messaggio_ricevuta")
    private String paymentReceiptId;
    @CsvBindByName(column = "causale_versamento")
    private String remittanceInformation;
    @CsvBindByName(column = "tipo_soggetto_pagatore")
    private PersonEntityType debtorEntityType;
    @CsvBindByName(column = "anagrafica_pagatore")
    private String debtorFullName;
    @CsvBindByName(column = "id_univoco_pagatore")
    private String debtorUniqueIdentifierCode;
    @CsvBindByName(column = "email_pagatore")
    private String debtorEmail;
    @CsvBindByName(column = "id_univoco_beneficiario")
    private String orgFiscalCode;
    @CsvBindByName(column = "anagrafica_versante")
    private String payerFullName;
    @CsvBindByName(column = "id_univoco_versante")
    private String payerUniqueIdentifierCode;
    @CsvBindByName(column = "IUV")
    private String iuv;
    @CsvBindByName(column = "esito_pagamento")
    private String paymentOutcome;
    @CsvBindByName(column = "codice_contesto_pagamento")
    private String creditorReferenceId;
}
