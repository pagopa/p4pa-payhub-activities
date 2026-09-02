package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

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

    @ParameterizedTest
    @ValueSource(strings = {"{invalid json structure"})
    void givenMalformedJsonWhenHandleSpontaneousFormThenThrowJacksonException(String input) {
		String code = "SF_CODE_INVALID";
        Long organizationId = 100L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure(input)
            .build();

        Exception exception = assertThrows(
            JacksonException.class,
            () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
        );

        assertNotNull(exception.getMessage());
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
