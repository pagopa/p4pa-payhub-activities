package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.pagopa_api.pa.pafornode.PaSendRTV2Request;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class ReceiptParserServiceTest {

  @Mock
  private ReceiptUnmarshallerService receiptUnmarshallerServiceMock;

  @InjectMocks
  private ReceiptParserService receiptParserService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenPathWhenParseReceiptPagopaFileThenOk(){
    //given
    Path path = Path.of("path");
    IngestionFlowFile ingestionFlowFile = podamFactory.manufacturePojo(IngestionFlowFile.class);
    PaSendRTV2Request paSendRTV2 = podamFactory.manufacturePojo(PaSendRTV2Request.class);

    Mockito.when(receiptUnmarshallerServiceMock.unmarshalReceiptPagopa(path.toFile())).thenReturn(paSendRTV2);

    //when
    PaSendRTV2Request response = receiptParserService.parseReceiptPagopaFile(path, ingestionFlowFile);

    //verify
    Assertions.assertEquals(paSendRTV2, response);
    Mockito.verify(receiptUnmarshallerServiceMock, Mockito.times(1)).unmarshalReceiptPagopa(path.toFile());
  }

}
