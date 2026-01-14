package it.gov.pagopa.payhub.activities.service.files;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.exceptions.*;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FileExceptionHandlerService {

    //CSV
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvErrorDetails {
        String errorCode;
        String errorMessage;
    }

    public CsvErrorDetails mapCsvExceptionToErrorCodeAndMessage(CsvException e) {
        switch (e) {
            case CsvRequiredFieldEmptyException csvRequiredFieldEmptyException -> {
                return buildErrorDetailsFromCsvRequiredFields(csvRequiredFieldEmptyException);
            }
            case CsvDataTypeMismatchException csvDataTypeMismatchException -> {
                Class<?> destinationClass = csvDataTypeMismatchException.getDestinationClass();
                String fieldType = destinationClass != null
                        ? destinationClass.getSimpleName()
                        : "unknown";

                Object sourceValue = csvDataTypeMismatchException.getSourceObject();

                String message = String.format("Impossibile convertire il valore '%s' nel tipo '%s'", sourceValue, fieldType);

                return new CsvErrorDetails(FileErrorCode.CSV_DATA_TYPE_MISMATCH.name(), message);
            }
            case CsvValidationException csvValidationException -> {
                return new CsvErrorDetails(FileErrorCode.CSV_VALIDATION_ERROR.name(),
                        "Validazione fallita: " + csvValidationException.getMessage());
            }
            case CsvConstraintViolationException csvConstraintViolationException -> {
                return new CsvErrorDetails(FileErrorCode.CSV_CONSTRAINT_VIOLATION.name(),
                        "Vincolo violato: " + csvConstraintViolationException.getMessage());
            }
            case null, default -> {
                StringBuilder message = new StringBuilder("Errore generico nella lettura del file");

                if(e == null){
                    return new CsvErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(), message.toString());
                }
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    message.append(": ").append(e.getMessage());
                }

                Throwable cause = e.getCause();
                if (cause != null && cause.getMessage() != null && !cause.getMessage().isEmpty()) {
                    message.append(". Causa: ").append(cause.getMessage());
                }

                return new CsvErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(), message.toString());
            }
        }
    }

    private CsvErrorDetails buildErrorDetailsFromCsvRequiredFields(CsvRequiredFieldEmptyException exception) {
        String message = exception.getMessage();

        CsvErrorDetails messageBasedError = handleMessageBasedErrors(message);
        if (messageBasedError != null) {
            return messageBasedError;
        }

        return handleFieldBasedErrors(exception);
    }

    private CsvErrorDetails handleMessageBasedErrors(String message) {
        if (message == null) {
            return null;
        }

        if (message.contains("Number of data fields does not match")) {
            return new CsvErrorDetails(FileErrorCode.CSV_COLUMN_COUNT_MISMATCH.name(),
                    "Il numero di colonne nella riga non corrisponde al numero atteso");
        }

        if (message.contains("Header is missing required fields")) {
            String fields = extractBetween(message, '[', ']');
            return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_HEADER.name(),
                    fields != null ? String.format("Nell'header manca il campo obbligatorio '%s'", fields)
                            : "Nell'header mancano uno o più campi obbligatori");
        }

        if (message.contains("is mandatory but no value was provided")) {
            String field = extractBetween(message, '\'', '\'');
            return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                    field != null ? String.format("Il campo obbligatorio '%s' e' vuoto", field)
                            : "Un campo obbligatorio e' vuoto o mancante");
        }

        return null;
    }

    private String extractBetween(String text, char startChar, char endChar) {
        try {
            int start = text.indexOf(startChar);
            int end = text.indexOf(endChar, start + 1);
            return (start != -1 && end > start) ? text.substring(start + 1, end).trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private CsvErrorDetails handleFieldBasedErrors(CsvRequiredFieldEmptyException exception) {
        Field field = exception.getDestinationField();

        if (field == null) {
            return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                    "Un campo obbligatorio e' vuoto o mancante: " + exception.getMessage());
        }

        CsvBindByPosition posAnnot = field.getAnnotation(CsvBindByPosition.class);
        if (posAnnot != null) {
            return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                    String.format("Il campo obbligatorio '%s' alla posizione %d e' vuoto o mancante",
                            field.getName(), posAnnot.position()));
        }

        CsvBindByName nameAnnot = field.getAnnotation(CsvBindByName.class);
        if (nameAnnot != null) {
            return handleNameAnnotation(field.getName(), nameAnnot.column(), exception.getLineNumber());
        }

        return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                "Un campo obbligatorio e' vuoto o mancante: " + exception.getMessage());
    }

    private CsvErrorDetails handleNameAnnotation(String fieldName, String columnName, long lineNumber) {
        String displayName = columnName.isEmpty() ? fieldName : columnName;

        if (lineNumber == 0) {
            return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_HEADER.name(),
                    String.format("Nell'header manca il campo obbligatorio '%s'", displayName));
        }

        return new CsvErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                String.format("Il campo obbligatorio '%s' e' vuoto", displayName));
    }

    //XML
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class XmlErrorDetails {
        String errorCode;
        String errorMessage;
    }

    private String extractTechnicalMessage(String fullMessage) {
        if (fullMessage == null) return "";

        int colonIndex = fullMessage.indexOf(": ");
        if (colonIndex == -1) return fullMessage;

        String afterColon = fullMessage.substring(colonIndex + 2);
        return afterColon.startsWith("SAXParseException: ")
                ? afterColon.substring("SAXParseException: ".length())
                : afterColon;
    }

    public XmlErrorDetails mapXmlParsingExceptionToErrorCodeAndMessage(String msg) {
        msg = extractTechnicalMessage(msg);

        if (!msg.startsWith("cvc-")) {
            return new XmlErrorDetails(FileErrorCode.XML_GENERIC_ERROR.name(),
                    "Errore di validazione XML: " + (msg.substring(0, Math.min(msg.length(), 100))));
        }

        String value = extract(msg, "Value '([^']*)'");
        String type = extract(msg, "for type '([^']+)'");

        if (msg.contains("cvc-pattern")) return handlePatternError(msg, value, type);
        if (msg.contains("cvc-complex-type")) return handleComplexTypeError(msg);
        if (msg.contains("cvc-datatype")) return handleDatatypeError(msg);
        if (msg.contains("Length-valid")) return handleLengthError(msg, type);
        if (msg.contains("Inclusive-valid") || msg.contains("Exclusive-valid")) return handleRangeError(msg, value, type);
        if (msg.contains("cvc-enumeration")) return handleEnumerationError(msg, value);
        if (msg.contains("cvc-fractionDigits")) return handleFractionDigitsError(msg, value);

        return new XmlErrorDetails(FileErrorCode.XML_GENERIC_ERROR.name(),
                "Errore di validazione XML" + (type != null ? " campo '" + type + "'" : ""));
    }

    private static XmlErrorDetails handlePatternError(String msg, String value, String type) {
        String pattern = extract(msg, "pattern '([^']+)'");
        return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                String.format("Il valore '%s' del campo '%s' non rispetta il formato '%s'", value, type, pattern));
    }

    private static XmlErrorDetails handleComplexTypeError(String msg) {
        String element = extract(msg, "element '([^']+)'");
        String expected = extract(msg, "One of '\\{([^}]+)\\}'");
        if (element != null && expected != null) {
            return new XmlErrorDetails(FileErrorCode.XML_MISSING_REQUIRED_FIELD.name(),
                    String.format("Elemento '%s' non valido: e' atteso l'elemento '%s'", element, expected));
        }
        return new XmlErrorDetails(FileErrorCode.XML_MISSING_REQUIRED_FIELD.name(),
                "Errore di validazione XML: struttura complessa non valida");
    }

    private static XmlErrorDetails handleDatatypeError(String msg) {
        String val = extract(msg, "'([^']+)' is not a valid value");
        String datatype = extract(msg, "for '([^']+)'");
        return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                String.format("Il valore '%s' non e' valido per il tipo '%s'", val, datatype));
    }

    private static XmlErrorDetails handleLengthError(String msg, String type) {
        String actualLen = extract(msg, "length = '(\\d+)'");
        if (actualLen == null) return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                "Errore di lunghezza campo");

        String op = msg.contains("min") ? "inferiore alla minima" : "superiore alla massima";
        String expectedLen = extract(msg, "(?:minLength|maxLength) '(\\d+)'");

        String base = expectedLen != null
                ? String.format("Lunghezza campo '%s' %s richiesta '%s'", actualLen, op, expectedLen)
                : String.format("Lunghezza campo '%s' non valida", actualLen);

        if (type != null) base += " per il campo '" + type + "'";
        if ("0".equals(actualLen)) base += " - campo vuoto ma obbligatorio";

        return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(), base);
    }

    private static XmlErrorDetails handleRangeError(String msg, String value, String type) {
        String op = msg.contains("max") ? "supera il massimo" : "e' sotto il minimo";
        String limit = extract(msg, "(?:maxInclusive|minInclusive|maxExclusive|minExclusive) '([^']+)'");

        String base = String.format("Il valore '%s' %s consentito '%s'", value, op, limit);
        if (type != null) base += " per il campo '" + type + "'";

        return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(), base);
    }

    private static XmlErrorDetails handleEnumerationError(String msg, String value) {
        String enumValues = extract(msg, "enumeration '\\[([^\\]]+)\\]'");
        if (value != null && enumValues != null) {
            return new XmlErrorDetails(FileErrorCode.XML_CONSTRAINT_VIOLATION.name(),
                    String.format("Il valore '%s' non e' tra i valori ammessi [%s]", value, enumValues));
        }
        return new XmlErrorDetails(FileErrorCode.XML_CONSTRAINT_VIOLATION.name(),
                "Errore di validazione: valore non tra quelli ammessi");
    }

    private static XmlErrorDetails handleFractionDigitsError(String msg, String value) {
        if (value != null) {
            Matcher m = Pattern.compile("has (\\d+) fraction.*limited to (\\d+)").matcher(msg);
            if (m.find()) {
                return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                        String.format("Il valore '%s' ha %s decimali ma il massimo consentito e' %s",
                        value, m.group(1), m.group(2)));
            }
        }
        return new XmlErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                "Errore: numero di decimali non valido");
    }

    private static String extract(String text, String regex) {
        try {
            Matcher m = Pattern.compile(regex).matcher(text);
            return m.find() ? m.group(1) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
