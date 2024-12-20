package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.ClassifyResultFaker.buildTransfer2ClassifyDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivityTest {
    @Mock
    private PaymentsReportingDao paymentsReportingDao;

    @Mock
    private ClassificationDao classificationDao;

    private IufClassificationActivity iufClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final Long TREASURYID = 1L;
    private static final String FLOWIDENTIFIERCODE = "FLOW";


    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingDao, classificationDao);
    }


    @Test
    void saveClassificationSuccess() {
        PaymentsReportingDTO expectedPaymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
        List<PaymentsReportingDTO> expectedPaymentsReportingDTOS = new ArrayList<>();
        expectedPaymentsReportingDTOS.add(expectedPaymentsReportingDTO);

        Transfer2ClassifyDTO expectedTransfer2ClassifyDTO = buildTransfer2ClassifyDTO(ORGANIZATIONID);
        List<Transfer2ClassifyDTO>  expectedTransfer2ClassifyDTOS = new ArrayList<>();
        expectedTransfer2ClassifyDTOS.add(expectedTransfer2ClassifyDTO);

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .transfers2classify(expectedTransfer2ClassifyDTOS)
                        .success(true)
                        .build();

        when(paymentsReportingDao.findByOrganizationIdAndIuf(ORGANIZATIONID, FLOWIDENTIFIERCODE))
                .thenReturn(expectedPaymentsReportingDTOS);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, FLOWIDENTIFIERCODE);
        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }

    @Test
    void saveClassificationNoPayments() {
        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .transfers2classify(new ArrayList<>())
                        .success(true)
                        .build();

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, FLOWIDENTIFIERCODE);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }
}

