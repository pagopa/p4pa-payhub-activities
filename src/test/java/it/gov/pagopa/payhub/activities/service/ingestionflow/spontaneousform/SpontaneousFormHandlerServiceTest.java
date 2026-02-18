package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void handleSpontaneousForm(String testName,
					        Long organizationId,
					        String code,
					        String jsonStructure,
					        Long expectedId,
					        Class<? extends Exception> expectedException,
					        String expectedExceptionMessage,
					        boolean shouldVerifyCreatedForm,
					        boolean shouldReturnNullOrFormWithoutId) {
        // Given
        DebtPositionTypeOrgIngestionFlowFileDTO row = DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .spontaneousFormCode(code)
            .spontaneousFormStructure(jsonStructure)
            .build();

        if (expectedId != null && !shouldVerifyCreatedForm) {
            SpontaneousForm existingForm = SpontaneousForm.builder()
                .spontaneousFormId(expectedId)
                .organizationId(organizationId)
                .code(code)
                .build();

            Mockito.when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
                .thenReturn(existingForm);
        } else {
            Mockito.when(spontaneousFormServiceMock.findByOrganizationIdAndCode(organizationId, code))
                .thenReturn(null);

            if (shouldVerifyCreatedForm) {
                SpontaneousForm createdForm = SpontaneousForm.builder()
                    .spontaneousFormId(expectedId)
                    .organizationId(organizationId)
                    .code(code)
                    .build();

                Mockito.when(spontaneousFormServiceMock.createSpontaneousForm(Mockito.any(SpontaneousForm.class)))
                    .thenReturn(createdForm);
            } else if (shouldReturnNullOrFormWithoutId) {
                if ("whenNoExistingFormAndCreateServiceReturnsNullThenReturnNull".equals(testName)) {
                    Mockito.when(spontaneousFormServiceMock.createSpontaneousForm(Mockito.any(SpontaneousForm.class)))
                        .thenReturn(null);
                } else {
                    SpontaneousForm createdFormWithoutId = SpontaneousForm.builder()
                        .organizationId(organizationId)
                        .code(code)
                        .build();

                    Mockito.when(spontaneousFormServiceMock.createSpontaneousForm(Mockito.any(SpontaneousForm.class)))
                        .thenReturn(createdFormWithoutId);
                }
            }
        }

        // When & Then
        if (expectedException != null) {
            Exception exception = Assertions.assertThrows(
                expectedException,
                () -> spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row)
            );

            Assertions.assertTrue(exception.getMessage().contains(code));
            if (expectedExceptionMessage != null) {
                Assertions.assertTrue(exception.getMessage().contains(expectedExceptionMessage));
            }
            Mockito.verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
        } else {
            Long result = spontaneousFormHandlerService.handleSpontaneousForm(organizationId, row);

            if (shouldReturnNullOrFormWithoutId) {
                Assertions.assertNull(result);
                Mockito.verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
                Mockito.verify(spontaneousFormServiceMock).createSpontaneousForm(Mockito.any(SpontaneousForm.class));
            } else if (shouldVerifyCreatedForm) {
                Assertions.assertEquals(expectedId, result);

                ArgumentCaptor<SpontaneousForm> formCaptor = ArgumentCaptor.forClass(SpontaneousForm.class);
                Mockito.verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
                Mockito.verify(spontaneousFormServiceMock).createSpontaneousForm(formCaptor.capture());

                SpontaneousForm capturedForm = formCaptor.getValue();
                Assertions.assertEquals(organizationId, capturedForm.getOrganizationId());
                Assertions.assertEquals(code, capturedForm.getCode());
                Assertions.assertNotNull(capturedForm.getStructure());
                Assertions.assertNull(capturedForm.getDictionary());
            } else {
                Assertions.assertEquals(expectedId, result);
                Mockito.verify(spontaneousFormServiceMock).findByOrganizationIdAndCode(organizationId, code);
            }
        }
    }

	static Stream<Arguments> provideTestCases() {
		return Stream.of(
			Arguments.of(
				"whenExistingFormFoundThenReturnExistingId",
				100L,
				"SF_CODE_001",
				"{\"fields\":[]}",
				999L,
				null,
				null,
				false,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndValidJsonThenCreateAndReturnNewId",
				100L,
				"SF_CODE_NEW",
				"{\"fields\":[{\"name\":\"field1\",\"type\":\"text\"}]}",
				555L,
				null,
				null,
				true,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndComplexValidJsonThenCreateSuccessfully",
				100L,
				"SF_CODE_COMPLEX",
				"{\"fields\":[{\"name\":\"field1\",\"type\":\"text\",\"required\":true},{\"name\":\"field2\",\"type\":\"number\"}],\"validation\":{\"minFields\":1}}",
				777L,
				null,
				null,
				true,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndInvalidJsonThenThrowInvalidValueException",
				100L,
				"SF_CODE_INVALID",
				"{invalid json structure",
				null,
				InvalidValueException.class,
				"Error parsing spontaneous form JSON structure",
				false,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndEmptyJsonThenThrowInvalidValueException",
				100L,
				"SF_CODE_EMPTY",
				"",
				null,
				InvalidValueException.class,
				null,
				false,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndNullJsonThenThrowInvalidValueException",
				100L,
				"SF_CODE_NULL",
				null,
				null,
				InvalidValueException.class,
				null,
				false,
				false
			),
			Arguments.of(
				"whenNoExistingFormAndCreateServiceReturnsNullThenReturnNull",
				100L,
				"SF_CODE_FAIL",
				"{\"fields\":[]}",
				null,
				null,
				null,
				false,
				true
			),
			Arguments.of(
				"whenNoExistingFormAndCreateServiceReturnsFormWithoutIdThenReturnNull",
				100L,
				"SF_CODE_NO_ID",
				"{\"fields\":[]}",
				null,
				null,
				null,
				false,
				true
			)
		);
	}
}




