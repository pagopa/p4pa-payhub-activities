package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.exportflow.ExportFileExpirationHandlerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportFileExpirationHandlerActivityTest {

  @Mock
  private ExportFileExpirationHandlerService exportFileExpirationHandlerServiceMock;

  @InjectMocks
  private ExportFileExpirationHandlerActivityImpl exportFileExpirationHandlerActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;
  private static final String ERROR_DESCRIPTION ="ERROR_DESCRIPTION";

  @Test
  void givenValidIdThenOk(){
    //given
    Mockito.doNothing().when(exportFileExpirationHandlerServiceMock).handleExpiration(VALID_ID, ERROR_DESCRIPTION);
    //when
    exportFileExpirationHandlerActivity.handleExpiration(VALID_ID, ERROR_DESCRIPTION);
    //verify
    Mockito.verify(exportFileExpirationHandlerServiceMock, Mockito.times(1)).handleExpiration(VALID_ID, ERROR_DESCRIPTION);
  }

  @Test
  void givenGenericErrorWhenFilesDeleteThenThrowException(){
    //given
    Mockito.doThrow(new IllegalStateException("Cannot delete file")).when(exportFileExpirationHandlerServiceMock).handleExpiration(VALID_ID, ERROR_DESCRIPTION);
    //when
    Assertions.assertThrows(IllegalStateException.class, () -> exportFileExpirationHandlerActivity.handleExpiration(VALID_ID, ERROR_DESCRIPTION));
  }

  @Test
  void givenInvalidIdThenThrowNotFoundException(){
    //given
    Mockito.doThrow(new ExportFileNotFoundException("File not found")).when(exportFileExpirationHandlerServiceMock).handleExpiration(INVALID_ID, ERROR_DESCRIPTION);
    //when
    Assertions.assertThrows(ExportFileNotFoundException.class, () -> exportFileExpirationHandlerActivity.handleExpiration(INVALID_ID, ERROR_DESCRIPTION));
  }

}
