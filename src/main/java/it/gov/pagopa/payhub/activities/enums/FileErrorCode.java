package it.gov.pagopa.payhub.activities.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum FileErrorCode {

    //CSV
    CSV_GENERIC_ERROR("Errore generico nella lettura del file%s"),
    CSV_MISSING_REQUIRED_FIELD,
    CSV_MISSING_REQUIRED_HEADER,
    CSV_COLUMN_COUNT_MISMATCH("Il numero di colonne nella riga non corrisponde al numero atteso"),
    CSV_DATA_TYPE_MISMATCH("Impossibile convertire il valore '%s' nel tipo '%s'"),
    CSV_VALIDATION_ERROR("Validazione fallita: %s"),
    CSV_CONSTRAINT_VIOLATION("Vincolo violato: %s"),

    //XML
    XML_GENERIC_ERROR,
    XML_MISSING_REQUIRED_FIELD,
    XML_VALIDATION_ERROR,
    XML_CONSTRAINT_VIOLATION,

    // CONNECTOR ERROR
    GENERIC_ERROR,
    PROCESSING_ERROR,
    UNKNOWN_ERROR("Errore sconosciuto"),

    // workflow
    WORKFLOW_TERMINATED_WITH_FAILURE("Il workflow di sincronizzazione e' terminato con stato di errore"),
    WORKFLOW_TIMEOUT("Il workflow di sincronizzazione ha superato il numero massivo di tentativi"),

    // organization
    BROKER_MISMATCH("L'intermediario non e' correlato con l'ente"),
    BROKER_NOT_FOUND("L'intermediario non e' stato trovato"),
    ORGANIZATION_IPA_MISMATCH("Il codice IPA %s dell'ente non corrisponde a quello del file %s"),
    ORGANIZATION_IPA_DOES_NOT_EXISTS("L'ente con codice IPA %s non esiste"),
    ORGANIZATION_NOT_FOUND("L'ente non esiste"),
    ORGANIZATION_ALREADY_EXISTS("L'ente esiste gia'"),
    ORG_SIL_SERVICE_NOT_FOUND("L'Org Sil Service non e' stato trovato"),
    INVALID_ORGANIZATION_STATUS("L'ente non e' attivo"),
    INVALID_SEGREGATION_CODE("Codice di segregazione non valido"),
    MISSING_ORGANIZATION_FIELDS("Il logo, l'iban e il codice di segregazione dell'ente sono obbligatori"),

    // debt position
    DEBT_POSITION_BY_IUD_NOT_FOUND("La posizione debitoria con IUD %s non e' stata trovata per l'ente"),
    INVALID_DEBT_POSITION("Posizione debitoria non valida o con origine differente"),
    TOO_MANY_DEBT_POSITIONS("Sono presenti piu' posizioni debitorie relative allo stesso IUD"),
    DEBT_POSITION_NOT_FOUND("Posizione debitoria non trovata"),
    INVALID_DEBT_POSITION_STATUS("La posizione debitoria non e' in uno stato valido per l'operazione richiesta"),
    UNDETERMINED_DEBT_POSITION_STATUS("Non e' stato possibile determinare il nuovo stato della posizione debitoria"),
    DEBT_POSITION_ALREADY_EXISTS("La posizione debitoria esiste gia'"),
    MISSING_DEBT_POSITION_TYPE_ORG("Il tipo posizione debitoria impostato e' obbligatorio"),
    DIFFERENT_DEBTORS_IN_SAME_PO("Le rate di un'opzione di pagamento devono avere lo stesso debitore"),
    MULTIDEBTOR_DISABLED("Non e' possibile avere piu' di un debitore se la posizione debitoria non permette coobbligati"),

    // payment option
    PAYMENT_OPTION_NOT_FOUND("Opzione di pagamento non trovata"),
    INVALID_PAYMENT_OPTION_STATUS("L'opzione di pagamento non e' in uno stato valido per l'operazione richiesta"),
    UNDETERMINED_PAYMENT_OPTION_STATUS("Non e' stato possibile determinare il nuovo stato dell'opzione di pagamento"),
    MISSING_PAYMENT_OPTION("La posizione debitoria deve avere almeno un'opzione di pagamento"),
    DUPLICATED_PAYMENT_OPTION_INDEX("Due o più opzioni di pagamento hanno lo stesso indice"),

    // installment
    INSTALLMENT_NOT_FOUND("Rata non trovata"),
    INVALID_INSTALLMENT_STATUS("La rata non e' in uno stato valido per l'operazione richiesta"),
    INSTALLMENT_ALREADY_EXISTS("Esiste gia' una rata con stesso IUV o IUD"),
    INVALID_IUV("Lo IUV non e' valido"),
    MISSING_IUV("Lo IUV e' obbligatorio"),
    MISSING_INSTALLMENT("L'opzione di pagamento deve avere almeno una rata"),
    MISSING_REMITTANCE_INFORMATION("La causale e' obbligatoria"),
    INVALID_CENTS_AMOUNT("L'importo e' obbligatorio e maggiore di 0"),
    INVALID_AMOUNT("L'importo della rata non e' valido per il tipo posizione debitoria"),
    INVALID_BALANCE("Il bilancio non e' formalmente valido"),
    INVALID_LEGACY_PAYMENT_METADATA("Il campo relativo ai dati specifici riscossione non rispetta il formato"),
    INVALID_INSTALLMENT_AMOUNT_CENTS("La somma degli importi dei beneficiari deve essere uguale all'importo della rata"),
    MISSING_DUE_DATE("La data di scadenza e' obbligatoria per il tipo posizione debitoria impostato"),
    INVALID_DUE_DATE("La data di scadenza non puo' essere retroattiva"),
    MISSING_DEBTOR("Il debitore e' obbligatorio"),
    INVALID_VAT_CODE("Il codice fiscale o partita iva non e' valido"),
    INVALID_FULLNAME("Il nome del beneficiario e' obbligatorio"),
    INVALID_EMAIL("L'email non e' formattata correttamente"),
    INVALID_INSTALLMENT_NAV_MISMATCH("I campi codIuv e identificativoUnivocoVersamento non corrispondono con quelli della rata gia' presente nel sistema"),

    // transfer
    MISSING_TRANSFER("La rata deve avere almeno un beneficiario"),
    TOO_MANY_TRANSFERS_FOR_INSTALLMENT("Sono consentiti al massimo 5 beneficiari per rata"),
    DUPLICATED_TRANSFER_INDEX("Due o piu' beneficiari hanno lo stesso indice"),
    INVALID_TRANSFER_INDEX("Il beneficiario puo' avere indice compreso da 1 e 5"),
    INVALID_IBAN("L'iban non e' valido"),
    INVALID_POSTAL_IBAN("L'iban postale non e' valido"),
    MISSING_TAXONOMY_CATEGORY("Il codice tassonomico e' obbligatorio"),
    INVALID_TAXONOMY_CATEGORY("Il codice tassonomico non e' valido"),

    //receipt
    RECEIPT_NOT_FOUND("La ricevuta non e' stata trovata"),
    RECEIPT_IUV_MISMATCH("I campi codIuv e identificativoUnivocoVersamento devono essere uguali"),
    RECEIPT_ALREADY_ASSOCIATED_TO_ANOTHER_IUV("La ricevuta caricata e' gia' associata ad un altro IUV"),
    RECEIPT_ORG_MISMATCH("I campi identificativoDominio e cod_fiscale_pa1 devono essere uguali all'ente della ricevuta"),

    // debt position type
    DEBT_POSITION_TYPE_ALREADY_EXISTS("Il tipo posizione debitoria gia' esiste"),
    DEBT_POSITION_TYPE_NOT_FOUND("Il tipo posizione debitoria non e' stato trovato"),
    DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND("Il tipo posizione debitoria con codice %s non e' stato trovato"),

    // debt position type org
    DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS("Il tipo posizione debitoria impostato gia' esiste"),
    DEBT_POSITION_TYPE_ORG_NOT_FOUND("Il tipo posizione debitoria impostato non e' stato trovato per l'ente"),
    DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND("Il tipo posizione debitoria impostato con codice %s non e' stato trovato per l'ente"),
    INVALID_DEBT_POSITION_TYPE_ORG_CODE("Il tipo posizione debitoria impostato non e' valido per l'ente"),
    DEBT_POSITION_TYPE_ORG_UNAUTHORIZED("L'operatore non e' abilitato al tipo posizione debitoria impostato"),

    //classification
    ASSESSMENTS_REGISTRY_ALREADY_EXISTS("Il registro degli accertamenti esiste gia'"),

    IUF_ALREADY_ASSOCIATED("Lo IUF %s e' gia' associato ad un'altra tesoreria per l'ente con codice IPA %s"),

    //send notification
    NOTIFICATION_ALREADY_PROCESSED("La notifica SEND e' gia' stata processata"),
    NOTIFICATION_NOT_PROCESSED("Non e' stato possibile processare la notifica SEND"),

    //workflow-hub
    SEND_DELIVERY_CONFLICT("Workflow terminato con errore durante l'invio della notifica SEND"),
    SEND_STATUS_ERROR("Workflow terminato con errore durante il controllo dello stato della notifica SEND"),
    WORKFLOW_INTERNAL_ERROR("Errore generico"),
    INVALID_CONFIG("Configurazione non valida"),
    INVALID_EXECUTION_CONFIG("Configurazione di esecuzione non valida"),
    INVALID_EXECUTION_CONFIG_TYPE("Configurazione del tipo di esecuzione non valida"),
    INVALID_INGESTION_FLOW_FILE_TYPE("Tipologia di import non supportata"),
    INVALID_WORKFLOW_ID("Workflow ID non valido"),
    INVALID_ORGANIZATION_ID_OR_IUD("Workflow ID o IUD non valido"),
    INVALID_ORGANIZATION_ID_OR_IUF("Workflow ID o IUF non valido"),
    WORKFLOW_NOT_FOUND("Workflow non trovato"),
    WORKFLOW_TYPE_NOT_FOUND("Tipologia di workflow non trovato"),
    TOO_MANY_ATTEMPTS("Numero massimo di tentativi raggiunto"),
    JSON_SERIALIZATION_ERROR("Errore durante la serializzazione del JSON"),
    JSON_DESERIALIZATION_ERROR("Errore durante la deserializzazione del JSON"),
    SYNC_ERROR("Errore durante la sincronizzazione della rata di una posizione debitoria"),

    // p4pa-auth
    INVALID_TOKEN("Token non valido"),
    INVALID_A2A_TOKEN("Token a2a non valido"),
    INVALID_TOKEN_TYPE("Token type non valido"),
    INVALID_TOKEN_IAT("Campo IAT del Token non valido"),
    INVALID_TOKEN_EXP("Token scaduto"),
    INVALID_TOKEN_JTI("Token JTI non valido"),
    INVALID_CLIENT_ID("Client ID non valido"),
    INVALID_SUBJECT_TOKEN("Subject Token non valido"),
    INVALID_SUBJECT_TOKEN_TYPE("Subject Token Type non valido"),
    INVALID_SUBJECT_ISSUER("Subject Issuer non valido"),
    INVALID_SCOPE("Scope del Token non valido"),
    INVALID_AUDIENCE("Audience del Token non valido"),
    INVALID_ISSUER("Issuer non valido"),
    INVALID_PUBLIC_KEY("Chiave publica non valida"),
    INVALID_CLIENT_SECRET("Client Secret non valida"),
    INVALID_GRANT_TYPE("Token GrantType non valido"),
    CLIENT_CONFLICT("Client già presente"),
    USER_UNAUTHORIZED("Utente non autorizzato"),
    CLIENT_UNAUTHORIZED("Client non autorizzato"),
    TOO_MANY_REQUESTS("Troppe richieste inviate"),
    USER_NOT_FOUND("Utente non trovato"),
    OPERATOR_NOT_FOUND("Operatore non trovato"),
    CLIENT_NOT_FOUND("Client non trovato"),
    ROLES_NOT_FOUND("Nessun ruolo associato all'utente"),
    ORGANIZATION_ACCESS_NOT_FOUND("Nessuna informazione di accesso disponibile sull'ente"),

    //p4pa-pagopa-payments
    ACTUALIZATION_ERROR("Errore di attualizzazione"),
    INVALID_INSTALLMENT("Rata non trovata oppure con status di sincronizzazione non valido"),
    INVALID_SYNC_STATUS("Status di sincronizzazione non valido"),
    INVALID_FILE_NAME("Nome file non valido"),

    //p4pa-send-notification
    PERSONAL_DATA_NOT_FOUND("Dati personali non trovati"),
    UPLOAD_ERROR("Errore durante il caricamento del file da inviare"),
    DELETE_ERROR("Errore durante l'eliminazione della root directory"),
    INVALID_NOTIFICATION_STATUS("Errore sullo stato della notifica"),
    STATUS_ALREADY_PROCESSED("Stato già elaborato"),
    NOTIFICATION_NOT_FOUND("Notifica non trovata"),
    STREAMS_NOT_FOUND("Flussi non trovati"),
    FILE_NOT_FOUND("File non trovato"),
    INVALID_SIGNATURE("Il file non ha una firma valida"),

    IMMUTABLE_FIELD("Campi non modificabili"),
    TODO;

    private final String message;

    FileErrorCode(String message) {
        this.message = message;
    }

    FileErrorCode() {
        this.message = null;
    }

    public static Optional<FileErrorCode> fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(FileErrorCode.valueOf(code.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean hasDefaultMessage() {
        return message != null;
    }

    public String format(Object... args) {
        if (message == null) {
            return null;
        }
        return String.format(message, args);
    }

}
