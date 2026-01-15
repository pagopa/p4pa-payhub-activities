package it.gov.pagopa.payhub.activities.service.files;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.exceptions.*;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ErrorDetails {
        private String errorCode;
        private String errorMessage;
    }

    //CONNECTOR
    private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("\\[([A-Z_]+)\\]");

    public ErrorDetails mapExceptionToErrorCodeAndMessage(String exceptionMessage) {
        if (exceptionMessage == null || exceptionMessage.isEmpty()) {
            return new ErrorDetails(FileErrorCode.UNKNOWN_ERROR.name(), FileErrorCode.UNKNOWN_ERROR.getMessage());
        }
        try {
            Matcher matcher = ERROR_CODE_PATTERN.matcher(exceptionMessage);
            if (matcher.find()) {
                String code = matcher.group(1);
                return FileErrorCode.fromCode(code)
                        .filter(FileErrorCode::hasDefaultMessage)
                        .map(errorCode -> new ErrorDetails(errorCode.name(), errorCode.getMessage()))
                        .orElse(new ErrorDetails(code, exceptionMessage));
            } else {
                return new ErrorDetails(FileErrorCode.GENERIC_ERROR.name(), exceptionMessage);
            }
        } catch (Exception e) {
            return new ErrorDetails(FileErrorCode.GENERIC_ERROR.name(), exceptionMessage);
        }
    }

    //CSV
    public ErrorDetails mapCsvExceptionToErrorCodeAndMessage(CsvException e) {
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

                return new ErrorDetails(FileErrorCode.CSV_DATA_TYPE_MISMATCH.name(),
                        FileErrorCode.CSV_DATA_TYPE_MISMATCH.format(sourceValue, fieldType));
            }
            case CsvValidationException csvValidationException -> {
                return new ErrorDetails(FileErrorCode.CSV_VALIDATION_ERROR.name(),
                        FileErrorCode.CSV_VALIDATION_ERROR.format(csvValidationException.getMessage()));
            }
            case CsvConstraintViolationException csvConstraintViolationException -> {
                return new ErrorDetails(FileErrorCode.CSV_CONSTRAINT_VIOLATION.name(),
                        FileErrorCode.CSV_CONSTRAINT_VIOLATION.format(csvConstraintViolationException.getMessage()));
            }
            case null, default -> {
                String message = "";

                if (e != null) {
                    if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                        message = message + ": " + e.getMessage();
                    }
                    Throwable cause = e.getCause();
                    if (cause != null && cause.getMessage() != null && !cause.getMessage().isEmpty()) {
                        message = message + ". Causa: " + cause.getMessage();
                    }
                }

                return new ErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(),
                        FileErrorCode.CSV_GENERIC_ERROR.format(message));
            }
        }
    }

    private ErrorDetails buildErrorDetailsFromCsvRequiredFields(CsvRequiredFieldEmptyException exception) {
        String message = exception.getMessage();

        ErrorDetails messageBasedError = handleMessageBasedErrors(message);
        if (messageBasedError != null) {
            return messageBasedError;
        }

        return handleFieldBasedErrors(exception);
    }

    private ErrorDetails handleMessageBasedErrors(String message) {
        if (message == null) {
            return null;
        }

        if (message.contains("Number of data fields does not match")) {
            return new ErrorDetails(FileErrorCode.CSV_COLUMN_COUNT_MISMATCH.name(),
                    FileErrorCode.CSV_COLUMN_COUNT_MISMATCH.getMessage());
        }

        if (message.contains("Header is missing required fields")) {
            String fields = extractBetween(message, '[', ']');
            return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_HEADER.name(),
                    fields != null ? String.format("Nell'header manca il campo obbligatorio '%s'", fields)
                            : "Nell'header mancano uno o piu' campi obbligatori");
        }

        if (message.contains("is mandatory but no value was provided")) {
            String field = extractBetween(message, '\'', '\'');
            return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
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

    private ErrorDetails handleFieldBasedErrors(CsvRequiredFieldEmptyException exception) {
        Field field = exception.getDestinationField();

        if (field == null) {
            return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                    "Un campo obbligatorio e' vuoto o mancante: " + exception.getMessage());
        }

        CsvBindByPosition posAnnot = field.getAnnotation(CsvBindByPosition.class);
        if (posAnnot != null) {
            return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                    String.format("Il campo obbligatorio '%s' alla posizione %d e' vuoto o mancante",
                            field.getName(), posAnnot.position()));
        }

        CsvBindByName nameAnnot = field.getAnnotation(CsvBindByName.class);
        if (nameAnnot != null) {
            return handleNameAnnotation(field.getName(), nameAnnot.column(), exception.getLineNumber());
        }

        return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                "Un campo obbligatorio e' vuoto o mancante: " + exception.getMessage());
    }

    private ErrorDetails handleNameAnnotation(String fieldName, String columnName, long lineNumber) {
        String displayName = columnName.isEmpty() ? fieldName : columnName;

        if (lineNumber == 0) {
            return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_HEADER.name(),
                    String.format("Nell'header manca il campo obbligatorio '%s'", displayName));
        }

        return new ErrorDetails(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(),
                String.format("Il campo obbligatorio '%s' e' vuoto", displayName));
    }


    //XML
    private String extractTechnicalMessage(String fullMessage) {
        if (fullMessage == null) return "";

        int colonIndex = fullMessage.indexOf(": ");
        if (colonIndex == -1) return fullMessage;

        String afterColon = fullMessage.substring(colonIndex + 2);
        return afterColon.startsWith("SAXParseException: ")
                ? afterColon.substring("SAXParseException: ".length())
                : afterColon;
    }

    public ErrorDetails mapXmlParsingExceptionToErrorCodeAndMessage(String msg) {
        msg = extractTechnicalMessage(msg);

        if (!msg.startsWith("cvc-")) {
            return new ErrorDetails(FileErrorCode.XML_GENERIC_ERROR.name(),
                    "Errore di validazione XML: " + (msg.substring(0, Math.min(msg.length(), 150))));
        }

        String value = extract(msg, "Value '([^']*)'");
        String type = extract(msg, "for type '([^']+)'");

        if (msg.contains("cvc-pattern")) return handlePatternError(msg, value, type);
        if (msg.contains("cvc-complex-type")) return handleComplexTypeError(msg);
        if (msg.contains("cvc-datatype")) return handleDatatypeError(msg);
        if (msg.contains("Length-valid")) return handleLengthError(msg, type);
        if (msg.contains("Inclusive-valid") || msg.contains("Exclusive-valid"))
            return handleRangeError(msg, value, type);
        if (msg.contains("cvc-enumeration")) return handleEnumerationError(msg, value);
        if (msg.contains("cvc-fractionDigits")) return handleFractionDigitsError(msg, value);

        return new ErrorDetails(FileErrorCode.XML_GENERIC_ERROR.name(),
                "Errore di validazione XML" + (type != null ? " campo '" + type + "'" : ""));
    }

    private static ErrorDetails handlePatternError(String msg, String value, String type) {
        String pattern = extract(msg, "pattern '([^']+)'");
        return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                String.format("Il valore '%s' del campo '%s' non rispetta il formato '%s'", value, type, pattern));
    }

    private static ErrorDetails handleComplexTypeError(String msg) {
        String element = extract(msg, "element '([^']+)'");
        String expected = extract(msg, "One of '\\{([^}]+)\\}'");
        if (element != null && expected != null) {
            return new ErrorDetails(FileErrorCode.XML_MISSING_REQUIRED_FIELD.name(),
                    String.format("Elemento '%s' non valido: e' atteso l'elemento '%s'", element, expected));
        }
        return new ErrorDetails(FileErrorCode.XML_MISSING_REQUIRED_FIELD.name(),
                "Errore di validazione XML: struttura complessa non valida");
    }

    private static ErrorDetails handleDatatypeError(String msg) {
        String val = extract(msg, "'([^']+)' is not a valid value");
        String datatype = extract(msg, "for '([^']+)'");
        return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                String.format("Il valore '%s' non e' valido per il tipo '%s'", val, datatype));
    }

    private static ErrorDetails handleLengthError(String msg, String type) {
        String actualLen = extract(msg, "length = '(\\d+)'");
        if (actualLen == null) return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                "Errore di lunghezza campo");

        String op = msg.contains("min") ? "inferiore alla minima" : "superiore alla massima";
        String expectedLen = extract(msg, "(?:minLength|maxLength) '(\\d+)'");

        String base = expectedLen != null
                ? String.format("Lunghezza campo '%s' %s richiesta '%s'", actualLen, op, expectedLen)
                : String.format("Lunghezza campo '%s' non valida", actualLen);

        if (type != null) base += " per il campo '" + type + "'";
        if ("0".equals(actualLen)) base += " - campo vuoto ma obbligatorio";

        return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(), base);
    }

    private static ErrorDetails handleRangeError(String msg, String value, String type) {
        String op = msg.contains("max") ? "supera il massimo" : "e' sotto il minimo";
        String limit = extract(msg, "(?:maxInclusive|minInclusive|maxExclusive|minExclusive) '([^']+)'");

        String base = String.format("Il valore '%s' %s consentito '%s'", value, op, limit);
        if (type != null) base += " per il campo '" + type + "'";

        return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(), base);
    }

    private static ErrorDetails handleEnumerationError(String msg, String value) {
        String enumValues = extract(msg, "enumeration '\\[([^\\]]+)\\]'");
        if (value != null && enumValues != null) {
            return new ErrorDetails(FileErrorCode.XML_CONSTRAINT_VIOLATION.name(),
                    String.format("Il valore '%s' non e' tra i valori ammessi [%s]", value, enumValues));
        }
        return new ErrorDetails(FileErrorCode.XML_CONSTRAINT_VIOLATION.name(),
                "Errore di validazione: valore non tra quelli ammessi");
    }

    private static ErrorDetails handleFractionDigitsError(String msg, String value) {
        if (value != null) {
            Matcher m = Pattern.compile("has (\\d+) fraction.*limited to (\\d+)").matcher(msg);
            if (m.find()) {
                return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
                        String.format("Il valore '%s' ha %s decimali ma il massimo consentito e' %s",
                                value, m.group(1), m.group(2)));
            }
        }
        return new ErrorDetails(FileErrorCode.XML_VALIDATION_ERROR.name(),
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
