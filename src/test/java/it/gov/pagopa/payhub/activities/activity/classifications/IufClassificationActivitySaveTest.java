package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyResultDTO;
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

import static it.gov.pagopa.payhub.activities.utility.faker.ClassifyResultFaker.buildClassifyResultDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivitySaveTest {
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

        ClassifyResultDTO expectedClassifyResultDTO = buildClassifyResultDTO(ORGANIZATIONID);
        List<ClassifyResultDTO> expectedClassifyResultDTOS = new ArrayList<>();
        expectedClassifyResultDTOS.add(expectedClassifyResultDTO);

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .classifyResultDTOS(expectedClassifyResultDTOS)
                        .success(true)
                        .build();

        when(paymentsReportingDao.findByOrganizationIdFlowIdentifierCode(ORGANIZATIONID, FLOWIDENTIFIERCODE))
                .thenReturn(expectedPaymentsReportingDTOS);

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, FLOWIDENTIFIERCODE);
        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }

    @Test
    void saveClassificationNoPayments() {
        List<ClassifyResultDTO> classifyResultDTOS  = new ArrayList<>();

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .classifyResultDTOS(classifyResultDTOS)
                        .success(true)
                        .build();

        IufClassificationActivityResult iufClassificationActivityResult =
                iufClassificationActivity.classify(ORGANIZATIONID, TREASURYID, FLOWIDENTIFIERCODE);

        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }
}

