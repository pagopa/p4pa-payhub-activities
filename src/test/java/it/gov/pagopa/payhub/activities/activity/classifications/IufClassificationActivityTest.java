package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.utility.faker.ClassificationFaker;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
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
    private PaymentsReportingDao paymentsReportingDaoMock;

    @Mock
    private ClassificationDao classificationDaoMock;

    private IufClassificationActivity iufClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final Long TREASURYID = 1L;
    private static final String IUF = "IUF";

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingDaoMock, classificationDaoMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(paymentsReportingDaoMock,classificationDaoMock);
    }

    @Test
    void givenReportedTransferWhenClassifyThenOk() {
        ClassificationDTO classificationDTO = ClassificationFaker.buildClassificationDTO();

        PaymentsReportingDTO expectedPaymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
        List<PaymentsReportingDTO> expectedPaymentsReportingDTOS = new ArrayList<>();
        expectedPaymentsReportingDTOS.add(expectedPaymentsReportingDTO);

        List<Transfer2ClassifyDTO> expectedTransfer2ClassifyDTOS =
                expectedPaymentsReportingDTOS
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

        when(paymentsReportingDaoMock.findByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(expectedPaymentsReportingDTOS);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, IUF);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findByOrganizationIdAndIuf(ORGANIZATIONID, IUF);
        Mockito.verify(classificationDaoMock, Mockito.times(0)).save(classificationDTO);
    }

    @Test
    void givenNoReportedTransferWhenClassifyThenAnomalyClassificationSave() {
        ClassificationDTO classificationDTO = ClassificationFaker.buildClassificationDTO();

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .transfers2classify(new ArrayList<>())
                        .success(true)
                        .build();

        when(paymentsReportingDaoMock.findByOrganizationIdAndIuf(ORGANIZATIONID, IUF))
                .thenReturn(new ArrayList<>());

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, IUF);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);

        Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findByOrganizationIdAndIuf(ORGANIZATIONID, IUF);
        Mockito.verify(classificationDaoMock, Mockito.times(1)).save(classificationDTO);
    }
}

