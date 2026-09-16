package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivityTest {

    @Mock
    private PaymentsReportingService paymentsReportingServiceMock;
    @Mock
    private TransferClassificationStoreService transferClassificationStoreService;
    @Mock
    private TreasuryService treasuryServiceMock;

    private IufClassificationActivity iufClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final String TREASURYID = "treasuryId";
    private static final String IUF = "IUF";

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(
                paymentsReportingServiceMock,
                transferClassificationStoreService,
                treasuryServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                paymentsReportingServiceMock,
                transferClassificationStoreService,
                treasuryServiceMock);
    }

    @Test
    void givenReportedTransferWhenClassifyIufThenOk() {
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();
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

        when(treasuryServiceMock.getById(TREASURYID)).thenReturn(treasury);
        when(paymentsReportingServiceMock.getByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(expectedCollectionModelPaymentsReporting);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classifyIuf(ORGANIZATIONID, TREASURYID, IUF);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }

    @Test
    void givenNoReportedTransferWhenClassifyIufThenAnomalyClassificationSave() {
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();

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
                iufClassificationActivity.classifyIuf(ORGANIZATIONID, TREASURYID, IUF);


        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(transferClassificationStoreService).saveIufClassifications(treasury, List.of(ClassificationsEnum.TES_NO_IUF_OR_IUV));
    }

    @Test
    void givenNegativeBillAmountWhenClassifyIufThenSkipClassification() {
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();
        treasury.setBillAmountCents(-1L);

        IufClassificationActivityResult expectedResult = IufClassificationActivityResult.builder()
                .organizationId(ORGANIZATIONID)
                .transfers2classify(Collections.emptyList())
                .build();

        when(treasuryServiceMock.getById(TREASURYID)).thenReturn(treasury);

        IufClassificationActivityResult actualResult =
                iufClassificationActivity.classifyIuf(ORGANIZATIONID, TREASURYID, IUF);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void givenNullIufWhenClassifyIufThenTesNoMatchClassificationSave() {
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();
        String nullIuf = null;

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(ORGANIZATIONID)
                        .transfers2classify(new ArrayList<>())
                        .build();

        when(treasuryServiceMock.getById(TREASURYID)).thenReturn(treasury);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classifyIuf(ORGANIZATIONID, TREASURYID, nullIuf);

        assertEquals(expectedIufClassificationActivityResult, iufClassificationActivityResult);
        Mockito.verify(transferClassificationStoreService).saveIufClassifications(treasury, List.of(ClassificationsEnum.TES_NO_MATCH));
        Mockito.verifyNoInteractions(paymentsReportingServiceMock);
    }

    @Test
    void givenBlankIufWhenClassifyIufThenTesNoMatchClassificationSave() {
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();
        String blankIuf = "   ";

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(ORGANIZATIONID)
                        .transfers2classify(new ArrayList<>())
                        .build();

        when(treasuryServiceMock.getById(TREASURYID)).thenReturn(treasury);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classifyIuf(ORGANIZATIONID, TREASURYID, blankIuf);

        assertEquals(expectedIufClassificationActivityResult, iufClassificationActivityResult);
        Mockito.verify(transferClassificationStoreService).saveIufClassifications(treasury, List.of(ClassificationsEnum.TES_NO_MATCH));
        Mockito.verifyNoInteractions(paymentsReportingServiceMock);
    }
}
