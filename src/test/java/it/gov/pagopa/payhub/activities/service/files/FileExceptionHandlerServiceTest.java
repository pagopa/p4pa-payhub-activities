package it.gov.pagopa.payhub.activities.service.files;

import com.opencsv.exceptions.*;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FileExceptionHandlerServiceTest {

    private FileExceptionHandlerService service;

    @BeforeEach
    void setup() {
        service = new FileExceptionHandlerService();
    }

    // ==================== CSV TESTS ====================

    @Test
    void testCsvRequiredFieldEmpty_ColumnCountMismatch() {
        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                "Number of data fields does not match number of headers"
        );

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_COLUMN_COUNT_MISMATCH.name(), result.getErrorCode());
        assertEquals("Il numero di colonne nella riga non corrisponde al numero atteso", result.getErrorMessage());
    }

    @Test
    void testCsvRequiredFieldEmpty_WithPositionAnnotation() throws NoSuchFieldException {
        Field field = TestCsvBeanWithPosition.class.getDeclaredField("name");

        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                TestCsvBeanWithPosition.class,
                field,
                "Field 'name' is required"
        );

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(), result.getErrorCode());
        assertEquals("Il campo obbligatorio 'name' alla posizione 0 è vuoto o mancante", result.getErrorMessage());
    }

    @Test
    void testCsvRequiredFieldEmpty_WithNameAnnotation_HeaderMissing() throws NoSuchFieldException {
        Field field = TestCsvBeanWithName.class.getDeclaredField("email");

        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                TestCsvBeanWithName.class,
                field,
                "Field 'email' is required"
        );
        exception.setLineNumber(0); // Header line

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_MISSING_REQUIRED_HEADER.name(), result.getErrorCode());
        assertEquals("Nell'header manca il campo obbligatorio 'email_address'", result.getErrorMessage());
    }

    @Test
    void testCsvRequiredFieldEmpty_WithNameAnnotation_DataLineMissing() throws NoSuchFieldException {
        Field field = TestCsvBeanWithName.class.getDeclaredField("email");

        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                TestCsvBeanWithName.class,
                field,
                "Field 'email' is required"
        );
        exception.setLineNumber(5); // Data line

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(), result.getErrorCode());
        assertEquals("Il campo obbligatorio 'email_address' è vuoto", result.getErrorMessage());
    }

    @Test
    void testCsvRequiredFieldEmpty_WithNameAnnotation_EmptyColumnName() throws NoSuchFieldException {
        Field field = TestCsvBeanWithEmptyName.class.getDeclaredField("username");

        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                TestCsvBeanWithEmptyName.class,
                field,
                "Field 'username' is required"
        );
        exception.setLineNumber(1);

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(), result.getErrorCode());
        assertEquals("Il campo obbligatorio 'username' è vuoto", result.getErrorMessage());
    }

    @Test
    void testCsvRequiredFieldEmpty_NoFieldNoAnnotation() {
        CsvRequiredFieldEmptyException exception = new CsvRequiredFieldEmptyException(
                "Generic required field error"
        );

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_MISSING_REQUIRED_FIELD.name(), result.getErrorCode());
        assertEquals("Un campo obbligatorio è vuoto o mancante: Generic required field error", result.getErrorMessage());
    }

    @Test
    void testCsvDataTypeMismatch_WithDestinationClass() {
        CsvDataTypeMismatchException exception = new CsvDataTypeMismatchException("invalid", Integer.class,
                "Cannot convert");

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_DATA_TYPE_MISMATCH.name(), result.getErrorCode());
        assertEquals("Impossibile convertire il valore 'invalid' nel tipo 'Integer'", result.getErrorMessage());
    }

    @Test
    void testCsvDataTypeMismatch_WithoutDestinationClass() {
        CsvDataTypeMismatchException exception = new CsvDataTypeMismatchException("123.34", null,
                "Cannot convert");

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_DATA_TYPE_MISMATCH.name(), result.getErrorCode());
        assertEquals("Impossibile convertire il valore '123.34' nel tipo 'unknown'", result.getErrorMessage());
    }

    @Test
    void testCsvValidationException() {
        CsvValidationException exception = new CsvValidationException(
                "Email format is invalid"
        );

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_VALIDATION_ERROR.name(), result.getErrorCode());
        assertEquals("Validazione fallita: Email format is invalid", result.getErrorMessage());
    }

    @Test
    void testCsvConstraintViolationException() {
        CsvConstraintViolationException exception = new CsvConstraintViolationException(
                "Value exceeds maximum length"
        );

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_CONSTRAINT_VIOLATION.name(), result.getErrorCode());
        assertEquals("Vincolo violato: Value exceeds maximum length", result.getErrorMessage());
    }

    @Test
    void testCsvGenericException_WithMessage() {
        CsvException exception = new CsvException("Generic CSV error");

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_GENERIC_ERROR.name(), result.getErrorCode());
        assertEquals("Errore generico nella lettura del file: Generic CSV error", result.getErrorMessage());
    }

    @Test
    void testCsvGenericException_WithCause() {
        RuntimeException cause = new RuntimeException("Root cause message");
        CsvException exception = new TestCsvException("Wrapper message", cause);

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_GENERIC_ERROR.name(), result.getErrorCode());
        assertEquals("Errore generico nella lettura del file: Wrapper message. Causa: Root cause message", result.getErrorMessage());
    }

    @Test
    void testCsvGenericException_EmptyMessage() {
        CsvException exception = new CsvException("");

        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(exception);

        assertEquals(FileErrorCode.CSV_GENERIC_ERROR.name(), result.getErrorCode());
        assertEquals("Errore generico nella lettura del file", result.getErrorMessage());
    }

    @Test
    void testCsvNullException() {
        FileExceptionHandlerService.CsvErrorDetails result = service.mapCsvExceptionToErrorCodeAndMessage(null);

        assertEquals(FileErrorCode.CSV_GENERIC_ERROR.name(), result.getErrorCode());
        assertEquals("Errore generico nella lettura del file", result.getErrorMessage());
    }

    // ==================== XML TESTS ====================

    @ParameterizedTest
    @MethodSource("provideXmlValidationErrorTestCases")
    void testXmlValidationErrors(String message, String expectedErrorMessage) {
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertEquals(FileErrorCode.XML_VALIDATION_ERROR.name(), result.getErrorCode());
        assertEquals(expectedErrorMessage, result.getErrorMessage());
    }

    private static Stream<Arguments> provideXmlValidationErrorTestCases() {
        return Stream.of(
                // Pattern error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-pattern-valid: " +
                                "Value 'xxxxx' is not facet-valid with respect to pattern '[A-Z0-9]{11,16}' " +
                                "for type 'stCodiceFiscaleEnte'.",
                        "Il valore 'xxxxx' del campo 'stCodiceFiscaleEnte' non rispetta il formato '[A-Z0-9]{11,16}'"
                ),
                // Datatype error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-datatype-valid.1.2.1: " +
                                "'202-01-12' is not a valid value for 'date'.",
                        "Il valore '202-01-12' non è valido per il tipo 'date'"
                ),
                // MinLength error - empty field
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minLength-valid: " +
                                "Value '' with length = '0' is not facet-valid with respect to minLength '1' " +
                                "for type '#AnonType_end_to_end_id'.",
                        "Lunghezza campo '0' inferiore alla minima richiesta '1' per il campo '#AnonType_end_to_end_id' - campo vuoto ma obbligatorio"
                ),
                // MaxLength error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-maxLength-valid: " +
                                "Value 'ABCDEFGHIJKLMNOP' with length = '16' is not facet-valid with respect to maxLength '10' " +
                                "for type 'codiceIdentificativo'.",
                        "Lunghezza campo '16' superiore alla massima richiesta '10' per il campo 'codiceIdentificativo'"
                ),
                // MinLength error - non empty
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minLength-valid: " +
                                "Value 'AB' with length = '2' is not facet-valid with respect to minLength '5' " +
                                "for type 'codiceBreve'.",
                        "Lunghezza campo '2' inferiore alla minima richiesta '5' per il campo 'codiceBreve'"
                ),
                // MaxInclusive error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-maxInclusive-valid: " +
                                "Value '34' is not facet-valid with respect to maxInclusive '5' for type 'stIndice'",
                        "Il valore '34' supera il massimo consentito '5' per il campo 'stIndice'"
                ),
                // MinInclusive error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minInclusive-valid: " +
                                "Value '2' is not facet-valid with respect to minInclusive '10' for type 'importoMinimo'",
                        "Il valore '2' è sotto il minimo consentito '10' per il campo 'importoMinimo'"
                ),
                // MaxExclusive error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-maxExclusive-valid: " +
                                "Value '100' is not facet-valid with respect to maxExclusive '100' for type 'percentage'",
                        "Il valore '100' supera il massimo consentito '100' per il campo 'percentage'"
                ),
                // MinExclusive error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minExclusive-valid: " +
                                "Value '0' is not facet-valid with respect to minExclusive '0' for type 'positiveNumber'",
                        "Il valore '0' è sotto il minimo consentito '0' per il campo 'positiveNumber'"
                ),
                // FractionDigits error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-fractionDigits-valid: " +
                                "Value '150859348.15678' has 5 fraction digits, but the number of fraction digits has been limited to 2",
                        "Il valore '150859348.15678' ha 5 decimali ma il massimo consentito è 2"
                ),
                // Length error without actual length
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minLength-valid: " +
                                "Field is too short for type 'testType'",
                        "Errore di lunghezza campo"
                ),
                // Length error without expected length
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-minLength-valid: " +
                                "Value 'test' with length = '4' is invalid for type 'testType'",
                        "Lunghezza campo '4' non valida per il campo 'testType'"
                ),
                // FractionDigits error - missing value
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-fractionDigits-valid: " +
                                "Too many decimal places",
                        "Errore: numero di decimali non valido"
                ),
                // Range error without type
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-maxInclusive-valid: " +
                                "Value '150' is not facet-valid with respect to maxInclusive '100'",
                        "Il valore '150' supera il massimo consentito '100'"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideXmlMissingRequiredFieldTestCases")
    void testXmlMissingRequiredFieldErrors(String message, String expectedErrorMessage) {
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertEquals(FileErrorCode.XML_MISSING_REQUIRED_FIELD.name(), result.getErrorCode());
        assertEquals(expectedErrorMessage, result.getErrorMessage());
    }

    private static Stream<Arguments> provideXmlMissingRequiredFieldTestCases() {
        return Stream.of(
                // Complex type error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-complex-type.2.4.a: " +
                                "Invalid content was found starting with element 'pagine_totali'. One of '{pagina}' is expected.",
                        "Elemento 'pagine_totali' non valido: è atteso 'pagina'"
                ),
                // Complex type error - missing elements
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-complex-type.2.4.b: " +
                                "The content of element is not complete",
                        "Errore di validazione XML: struttura complessa non valida"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideXmlConstraintViolationTestCases")
    void testXmlConstraintViolationErrors(String message, String expectedErrorMessage) {
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertEquals(FileErrorCode.XML_CONSTRAINT_VIOLATION.name(), result.getErrorCode());
        assertEquals(expectedErrorMessage, result.getErrorMessage());
    }

    private static Stream<Arguments> provideXmlConstraintViolationTestCases() {
        return Stream.of(
                // Enumeration error
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-enumeration-valid: " +
                                "Value '2.5' is not facet-valid with respect to enumeration '[1.0, 1.1]'. " +
                                "It must be a value from the enumeration.",
                        "Il valore '2.5' non è tra i valori ammessi [1.0, 1.1]"
                ),
                // Enumeration error - missing value
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-enumeration-valid: " +
                                "Invalid enumeration value",
                        "Errore di validazione: valore non tra quelli ammessi"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideXmlGenericErrorTestCases")
    void testXmlGenericErrors(String message, String expectedErrorMessage) {
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertEquals(FileErrorCode.XML_GENERIC_ERROR.name(), result.getErrorCode());
        assertEquals(expectedErrorMessage, result.getErrorMessage());
    }

    private static Stream<Arguments> provideXmlGenericErrorTestCases() {
        return Stream.of(
                // Non CVC message
                Arguments.of(
                        "Error while parsing file GDC-test.xml: XML is malformed and not well-formed",
                        "Errore di validazione XML: XML is malformed and not well-formed"
                ),
                // Unknown CVC type with type
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-unknown-type: some error for type 'testType'",
                        "Errore di validazione XML campo 'testType'"
                ),
                // Unknown CVC type without type
                Arguments.of(
                        "Error while parsing file GDC-test.xml: SAXParseException: cvc-something-else: some error",
                        "Errore di validazione XML"
                )
        );
    }

    @Test
    void testExtractTechnicalMessage_WithSAXParseException() {
        String message = "Error while parsing file test.xml: SAXParseException: cvc-pattern-valid: some error";
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertNotNull(result);
    }

    @Test
    void testExtractTechnicalMessage_WithoutSAXParseException() {
        String message = "Error while parsing file test.xml: cvc-pattern-valid: some error";
        FileExceptionHandlerService.XmlErrorDetails result =
                service.mapXmlParsingExceptionToErrorCodeAndMessage(message);

        assertNotNull(result);
    }

    // ==================== TEST HELPER CLASSES ====================

    private static class TestCsvBeanWithPosition {
        @com.opencsv.bean.CsvBindByPosition(position = 0, required = true)
        private String name;
    }

    private static class TestCsvBeanWithName {
        @com.opencsv.bean.CsvBindByName(column = "email_address", required = true)
        private String email;
    }

    private static class TestCsvBeanWithEmptyName {
        @com.opencsv.bean.CsvBindByName(column = "", required = true)
        private String username;
    }

    private static class TestCsvException extends CsvException {
        public TestCsvException(String message) {
            super(message);
        }

        public TestCsvException(String message, Throwable cause) {
            super(message);
            if (cause != null) {
                initCause(cause);
            }
        }
    }
}