package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.util.faker.ClassificationFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Mock
    private TreasuryService treasuryServiceMock;

    private IufClassificationActivity iufClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final String TREASURYID = "treasuryId";
    private static final String IUF = "IUF";

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingServiceMock, classificationServiceMock, treasuryServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(paymentsReportingServiceMock, classificationServiceMock, treasuryServiceMock);
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
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();

        Classification expectedClassification = Classification.builder()
            .organizationId(ORGANIZATIONID)
            .treasuryId(TREASURYID)
            .iuf(IUF)
            .label("TES_NO_MATCH")
            .lastClassificationDate(LocalDate.now())
            .billDate(treasury.getBillDate())
            .regionValueDate(treasury.getRegionValueDate())
            .pspLastName(treasury.getPspLastName())
            .accountRegistryCode(treasury.getAccountRegistryCode())
            .billAmountCents(treasury.getBillAmountCents())
            .build();

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(ORGANIZATIONID)
                        .transfers2classify(new ArrayList<>())
                        .build();

        when(paymentsReportingServiceMock.getByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(CollectionModelPaymentsReporting.builder()
                        .embedded(PagedModelPaymentsReportingEmbedded.builder()
                                .paymentsReportings(List.of())
                                .build())
                        .build());

        when(treasuryServiceMock.getById(TREASURYID)).thenReturn(treasury);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, IUF);


        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATIONID, IUF);
        Mockito.verify(treasuryServiceMock, Mockito.times(1)).getById(TREASURYID);
        Mockito.verify(classificationServiceMock, Mockito.times(1)).save(expectedClassification);
    }
}

