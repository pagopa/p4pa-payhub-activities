package it.gov.pagopa.payhub.activities.activity.debtposition.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptRetrieverActivityTest {

    @Mock
    private ReceiptService receiptServiceMock;

    private ReceiptRetrieverActivity receiptRetrieverActivity;

    @BeforeEach
    void init() {
        receiptRetrieverActivity = new ReceiptRetrieverActivityImpl(receiptServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(receiptServiceMock);
    }

    @Test
    void whenRetrieveReceiptThenOk() {
        Long receiptId = 1L;
        ReceiptDTO expectedResponse = new ReceiptDTO();

        when(receiptServiceMock.getByReceiptId(receiptId))
                .thenReturn(expectedResponse);

        ReceiptDTO result = assertDoesNotThrow(
                () -> receiptRetrieverActivity.retrieveReceipt(receiptId));

        assertSame(expectedResponse, result);
    }
}
