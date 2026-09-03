package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.common.InvalidValueException;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeConflictException;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeInvalidValueException;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpontaneousFormHandlerServiceTest {

    @Mock
    private SpontaneousFormService spontaneousFormServiceMock;

    private SpontaneousFormHandlerService spontaneousFormHandlerService;

    @BeforeEach
    void setUp() {
        spontaneousFormHandlerService = new SpontaneousFormHandlerService(spontaneousFormServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(spontaneousFormServiceMock);
    }

    @Test
    void givenValidInputWhenHandleSpontaneousFormThenReturnMatchedId() {
        Long organizationId = 100L;
        String code = "SF_CODE_001";
        Long expectedId = 999L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{\"fields\":[]}")
            .build();

        SpontaneousForm existingForm = SpontaneousForm.builder()
            .spontaneousFormId(expectedId)
            .organizationId(organizationId)
            .code(code)
            .structure(mock(JsonNode.class))
            .build();

        when(spontaneousFormServiceMock.matchOrSaveSpontaneousForm(any(SpontaneousForm.class)))
            .thenReturn(existingForm);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertEquals(expectedId, result);
    }

    @Test
    void givenValidJsonWhenHandleSpontaneousFormThenCallMatchOrSaveWithExpectedForm() {
        Long organizationId = 100L;
        String code = "SF_CODE_NEW";
        String jsonStructure = "{\"fields\":[{\"name\":\"field1\",\"type\":\"text\"}]}";
        Long expectedId = 555L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure(jsonStructure)
            .build();

        SpontaneousForm createdForm = SpontaneousForm.builder()
            .spontaneousFormId(expectedId)
            .organizationId(organizationId)
            .code(code)
            .structure(mock(JsonNode.class))
            .build();

        ArgumentCaptor<SpontaneousForm> formCaptor = ArgumentCaptor.forClass(SpontaneousForm.class);
        when(spontaneousFormServiceMock.matchOrSaveSpontaneousForm(formCaptor.capture()))
            .thenReturn(createdForm);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertEquals(expectedId, result);

        SpontaneousForm capturedForm = formCaptor.getValue();
        assertEquals(organizationId, capturedForm.getOrganizationId());
        assertEquals(code, capturedForm.getCode());
        assertEquals(jsonStructure, capturedForm.getStructure().toString());
        assertNull(capturedForm.getDictionary());
    }

    @Test
    void givenMalformedJsonWhenHandleSpontaneousFormThenThrowInvalidValueException() {
		String code = "SF_CODE_INVALID";
        Long organizationId = 100L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{invalid json structure")
            .build();

        InvalidValueException exception = assertThrows(
            InvalidValueException.class,
            () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
        );

        assertEquals("INVALID_VALUE", exception.getCode());
        assertTrue(exception.getMessage().contains("Error parsing spontaneous form JSON structure for code " + code));
    }

    @Test
    void givenRestInvokeInvalidValueExceptionWhenHandleSpontaneousFormThenThrowInvalidValueExceptionWithSameCode() {
        String code = "SF_CODE_INVALID_VALUE";
        Long organizationId = 100L;
        String exceptionCode = "DOWNSTREAM_INVALID_VALUE";
        String errormessage = "ERRORMESSAGE";

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{\"fields\":[]}")
            .build();

        when(spontaneousFormServiceMock.matchOrSaveSpontaneousForm(any(SpontaneousForm.class)))
            .thenThrow(new RestInvokeInvalidValueException("APPNAME", HttpStatus.BAD_REQUEST, "ERROR", exceptionCode, errormessage, null));

        InvalidValueException exception = assertThrows(
            InvalidValueException.class,
            () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
        );

        assertEquals(exceptionCode, exception.getCode());
        assertEquals(errormessage, exception.getMessage());
    }

    @Test
    void givenRestInvokeConflictExceptionWhenHandleSpontaneousFormThenThrowInvalidValueExceptionWithSameCode() {
        String code = "SF_CODE_CONFLICT";
        Long organizationId = 100L;
        String exceptionCode = "DOWNSTREAM_CONFLICT";
        String errormessage = "ERRORMESSAGE";

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{\"fields\":[]}")
            .build();

        when(spontaneousFormServiceMock.matchOrSaveSpontaneousForm(any(SpontaneousForm.class)))
            .thenThrow(new RestInvokeConflictException("APPNAME", HttpStatus.CONFLICT, "ERROR", exceptionCode, errormessage, null));

        InvalidValueException exception = assertThrows(
            InvalidValueException.class,
            () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
        );

        assertEquals(exceptionCode, exception.getCode());
        assertEquals(errormessage, exception.getMessage());
    }

    @Test
    void givenMatchOrSaveReturnsNullWhenHandleSpontaneousFormThenReturnNull() {
        Long organizationId = 100L;
        String code = "SF_CODE_FAIL";

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{\"fields\":[]}")
            .build();

        when(spontaneousFormServiceMock.matchOrSaveSpontaneousForm(any(SpontaneousForm.class)))
            .thenReturn(null);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertNull(result);
    }

    @Test
    void givenNoCodeWhenHandleSpontaneousFormThenReturnNull() {
        Long organizationId = 100L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
                .spontaneousFormStructure("{\"fields\":[]}")
                .build();

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertNull(result);
    }
}
