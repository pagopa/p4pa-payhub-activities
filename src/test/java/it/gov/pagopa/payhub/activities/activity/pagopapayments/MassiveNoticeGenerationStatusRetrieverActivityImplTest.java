package it.gov.pagopa.payhub.activities.activity.pagopapayments;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.MassiveNoticeGenerationStatusRetrieverActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.MassiveNoticeGenerationStatusRetrieverActivityImpl;
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
class MassiveNoticeGenerationStatusRetrieverActivityImplTest {
    @Mock
    private PrintPaymentNoticeService printPaymentNoticeServiceMock;

    private MassiveNoticeGenerationStatusRetrieverActivity activity;

    @BeforeEach
    void setUp() {
        activity = new MassiveNoticeGenerationStatusRetrieverActivityImpl(printPaymentNoticeServiceMock);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(printPaymentNoticeServiceMock);
    }

    @Test
    void givenValidOrgIdAndFolderIdWhenRetrieveNoticesGenerationStatusThenOk() {
        //given
        Long orgId = 1L;
        String pdfGeneratedId = "pdfGeneratedId";
        SignedUrlResultDTO signedUrlResultDTO = new SignedUrlResultDTO();
        signedUrlResultDTO.setSignedUrl("signedUrl");

        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(orgId, pdfGeneratedId)).thenReturn(signedUrlResultDTO);

        //when
        SignedUrlResultDTO result = activity.retrieveNoticesGenerationStatus(orgId, pdfGeneratedId);

        //then
        assertEquals(result, signedUrlResultDTO);
    }
}
