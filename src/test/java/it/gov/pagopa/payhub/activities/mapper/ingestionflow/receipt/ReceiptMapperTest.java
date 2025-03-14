package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
 class ReceiptMapperTest {

  @InjectMocks
  private ReceiptMapper receiptMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void givenPaSendRTV2RequestWhenMapThenOk(boolean isPayerNull) {
    //given
    PaSendRTV2Request request = podamFactory.manufacturePojo(PaSendRTV2Request.class);
    if(isPayerNull)
      request.getReceipt().setPayer(null);
    //fix due to the fact that the field setter has non-standard name
    request.getReceipt().setPSPCompanyName(podamFactory.manufacturePojo(String.class));
    request.getReceipt().getTransferList().getTransfers().forEach(t -> {
      t.setIBAN(podamFactory.manufacturePojo(String.class));
      t.setMBDAttachment(podamFactory.manufacturePojo(byte[].class));
    });
    //when
    ReceiptWithAdditionalNodeDataDTO response = receiptMapper.map(request);
    //verify
    TestUtils.checkNotNullFields(response, "receiptId", "ingestionFlowFileId", "creationDate", "updateDate", "payer");
    TestUtils.checkNotNullFields(response.getDebtor());
    if(!isPayerNull) {
      Assertions.assertNotNull(response.getPayer());
      TestUtils.checkNotNullFields(response.getPayer());
    }
    Assertions.assertEquals(request.getReceipt().getTransferList().getTransfers().size(), response.getTransfers().size());
    Assertions.assertEquals(request.getReceipt().getMetadata().getMapEntries().size(), response.getMetadata().size());
    response.getTransfers().forEach(TestUtils::checkNotNullFields);

  }
}
