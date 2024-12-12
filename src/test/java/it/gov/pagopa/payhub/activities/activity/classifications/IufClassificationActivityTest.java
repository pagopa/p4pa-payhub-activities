package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivityTest {

    @Mock
    private ReportingDao reportingDao;

    private IufClassificationActivity iufClassificationActivity;

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(reportingDao);
    }

    @Test
    void givenClassificationThenSuccess() {
        Long organizationId = 1L;
        String iuf = "IUF_TO_SEARCH";

        PaymentsReportingDTO firstClassification =
                PaymentsReportingDTO
                        .builder()
                        .organizationId(organizationId)
                        .creditorReferenceId(iuf)
                        .regulationId("REGULATION_ID")
                        .amountPaidCents(123L)
                        .transferIndex(1)
                        .flowIdentifierCode("FLOW")
                    .build();

        List<PaymentsReportingDTO> expectedClassificationDTOS = new ArrayList<>();
        expectedClassificationDTOS.add(firstClassification);

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .paymentsReportingDTOS(expectedClassificationDTOS)
                .success(true)
                .build();

        when(reportingDao.findByOrganizationIdFlowIdentifierCode(organizationId, iuf)).thenReturn(expectedClassificationDTOS)
            .thenReturn(expectedClassificationDTOS);

        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, iuf);

        assertEquals(expectedIufClassificationDTO, result);
    }

    @Test
    void givenClassificationOrganizationBlankThenFailed() {
        Long organizationId = 0L;
        String flowIdentifierCode = "IUF_TO_SEARCH";

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .paymentsReportingDTOS(new ArrayList<>())
                .success(false)
                .build();
        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, flowIdentifierCode);
        assertEquals(expectedIufClassificationDTO, result);
    }

    @Test
    void givenClassificationIufBlankThenFailed() {
        Long organizationId = 10L;
        String flowIdentifierCode = "";

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .paymentsReportingDTOS(new ArrayList<>())
                .success(false)
                .build();
        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, flowIdentifierCode);
        assertEquals(expectedIufClassificationDTO, result);
    }

}

