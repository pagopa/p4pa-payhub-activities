package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.util.faker.ClassificationFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelPaymentsReportingEmbedded;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivityTest {
    @Mock
    private PaymentsReportingService paymentsReportingServiceMock;

    @Mock
    private ClassificationService classificationServiceMock;

    private IufClassificationActivity iufClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final String TREASURYID = "treasuryId";
    private static final String IUF = "IUF";

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingServiceMock, classificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(paymentsReportingServiceMock, classificationServiceMock);
    }

    @Test
    void givenReportedTransferWhenClassifyThenOk() {
        Classification classificationDTO = ClassificationFaker.buildClassificationDTO();

        CollectionModelPaymentsReporting expectedCollectionModelPaymentsReporting = PaymentsReportingFaker.buildCollectionModelPaymentsReporting();

        List<PaymentsReporting> expectedPaymentsReportingS =expectedCollectionModelPaymentsReporting.getEmbedded().getPaymentsReportings();

        List<Transfer2ClassifyDTO> expectedTransfer2ClassifyDTOS =
                expectedPaymentsReportingS
                .stream()
                .map(paymentsReportingDTO ->
                    Transfer2ClassifyDTO.builder()
                        .iuv(paymentsReportingDTO.getIuv())
                        .iur(paymentsReportingDTO.getIur())
                        .transferIndex(paymentsReportingDTO.getTransferIndex())
                        .build())
                .toList();

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .transfers2classify(expectedTransfer2ClassifyDTOS)
                        .success(true)
                        .build();

        when(paymentsReportingServiceMock.getByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(expectedCollectionModelPaymentsReporting);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, IUF);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATIONID, IUF);
        Mockito.verify(classificationServiceMock, Mockito.times(0)).save(classificationDTO);
    }

    @Test
    void givenNoReportedTransferWhenClassifyThenAnomalyClassificationSave() {
        Classification classificationDTO = ClassificationFaker.buildClassificationDTO();

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .transfers2classify(new ArrayList<>())
                        .success(true)
                        .build();

        when(paymentsReportingServiceMock.getByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(CollectionModelPaymentsReporting.builder()
                        .embedded(PagedModelPaymentsReportingEmbedded.builder()
                                .paymentsReportings(List.of())
                                .build())
                        .build());

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, IUF);


        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATIONID, IUF);
        Mockito.verify(classificationServiceMock, Mockito.times(1)).save(classificationDTO);
    }
}

