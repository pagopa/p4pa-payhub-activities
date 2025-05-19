package it.gov.pagopa.payhub.activities.activity.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FolderStatusActivityImplTest {
    @Mock
    private PrintPaymentNoticeService printPaymentNoticeServiceMock;

    private FolderStatusActivity activity;

    @BeforeEach
    void setUp() {
        activity = new FolderStatusActivityImpl(printPaymentNoticeServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(printPaymentNoticeServiceMock);
    }

    @Test
    void givenValidOrgIdAndFolderIdWhenRetrieveFolderStatusThenOk() {
        //given
        Long orgId = 1L;
        String folderId = "folderId";
        SignedUrlResultDTO signedUrlResultDTO = new SignedUrlResultDTO();
        signedUrlResultDTO.setSignedUrl("signedUrl");

        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(orgId, folderId)).thenReturn(signedUrlResultDTO);

        //when
        SignedUrlResultDTO result = activity.retrieveFolderStatus(orgId, folderId);

        //then
        assertEquals(result, signedUrlResultDTO);
    }
}
