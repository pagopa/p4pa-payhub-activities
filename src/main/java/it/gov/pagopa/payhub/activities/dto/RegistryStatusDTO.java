package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryStatusDTO implements Serializable {

    public static final String STATO_ENTE_ESERCIZIO = "ESERCIZIO";
    public static final String STATO_ENTE_PRE_ESERCIZIO = "PRE-ESERCIZIO";

    public static final String STATO_TIPO_CARRELLO = "carrel";
    public static final String STATO_CARRELLO_PAGATO = "PAGATO";
    public static final String STATO_CARRELLO_NON_PAGATO = "NON_PAGATO";

    public static final String STATO_TIPO_DOVUTO = "dovuto";
    public static final String STATO_DOVUTO_DA_PAGARE = "INSERIMENTO_DOVUTO";
    public static final String STATO_DOVUTO_PAGAMENTO_INIZIATO = "PAGAMENTO_INIZIATO";
    public static final String STATO_DOVUTO_PREDISPOSTO = "PREDISPOSTO";
    public static final String STATO_DOVUTO_COMPLETATO = "COMPLETATO";
    public static final String STATO_DOVUTO_ANNULLATO = "ANNULLATO";
    public static final String STATO_DOVUTO_ERRORE = "ERROR_DOVUTO";
    public static final String STATO_DOVUTO_ABORT = "ABORT";
    public static final String STATO_DOVUTO_SCADUTO = "SCADUTO";
    public static final String STATO_DOVUTO_SCADUTO_ELABORATO = "SCADUTO_ELABORATO";
    public static final String STATO_TIPO_FLUSSO = "flusso";
    public static final String STATO_ENTE = "ente";

    private Long statusId;
    private String statusCode;
    private String statusDesc;
    private String statusType;
    private Timestamp creationDate;
    private Timestamp lastChangeDate;
}