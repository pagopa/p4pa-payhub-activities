package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousFormStructure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;

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
    void whenExistingFormFoundThenReturnExistingId() {
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
            .structure(new SpontaneousFormStructure())
            .code(code)
            .build();

        when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
            .thenReturn(existingForm);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertEquals(expectedId, result);
        verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
    }

    @Test
    void whenNoExistingFormAndValidJsonThenCreateAndReturnNewId() {
        Long organizationId = 100L;
        String code = "SF_CODE_NEW";
        String jsonStructure = "{\"fields\":[{\"name\":\"field1\",\"type\":\"text\"}]}";
        Long expectedId = 555L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure(jsonStructure)
            .build();

        when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
            .thenReturn(null);

        SpontaneousForm createdForm = SpontaneousForm.builder()
            .spontaneousFormId(expectedId)
            .organizationId(organizationId)
            .structure(new SpontaneousFormStructure())
            .code(code)
            .build();

        when(spontaneousFormServiceMock.createSpontaneousForm(any(SpontaneousForm.class)))
            .thenReturn(createdForm);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertEquals(expectedId, result);

        ArgumentCaptor<SpontaneousForm> formCaptor = ArgumentCaptor.forClass(SpontaneousForm.class);
        verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
        verify(spontaneousFormServiceMock).createSpontaneousForm(formCaptor.capture());

        SpontaneousForm capturedForm = formCaptor.getValue();
        assertEquals(organizationId, capturedForm.getOrganizationId());
        assertEquals(code, capturedForm.getCode());
        assertNotNull(capturedForm.getStructure());
        assertNull(capturedForm.getDictionary());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"{invalid json structure"})
    void whenNoExistingFormAndInvalidJsonThenThrowInvalidValueException(String input) {
		String code = "SF_CODE_INVALID";
        Long organizationId = 100L;

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure(input)
            .build();

        when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
            .thenReturn(null);

        Exception exception = assertThrows(
            InvalidValueException.class,
            () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
        );

        assertTrue(exception.getMessage().contains(code));
        verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
    }

    @Test
    void whenNoExistingFormAndCreateServiceReturnsNullThenReturnNull() {
        Long organizationId = 100L;
        String code = "SF_CODE_FAIL";

        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure("{\"fields\":[]}")
            .build();

        when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
            .thenReturn(null);

        when(spontaneousFormServiceMock.createSpontaneousForm(any(SpontaneousForm.class)))
            .thenReturn(null);

        Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

        assertNull(result);
        verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
        verify(spontaneousFormServiceMock).createSpontaneousForm(any(SpontaneousForm.class));
    }
}
