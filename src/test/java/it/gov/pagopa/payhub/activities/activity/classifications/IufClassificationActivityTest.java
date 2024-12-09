package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.ReportingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        String organizationId = "1";
        String iuf = "IUF_TO_SEARCH";

        ReportingDTO firstClassification =
                ReportingDTO.builder()
                    .iuv("IUV")
                    .orgId(Long.valueOf(organizationId))
                    .receiptId(2L)
                    .amount(10L)
                    .transferId(123L)
                    .transferIndex(1L)
                    .build();

        List<ReportingDTO> expectedClassificationDTOS = new ArrayList<>();
        expectedClassificationDTOS.add(firstClassification);

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .reportingDTOList(expectedClassificationDTOS)
                .success(true)
                .build();

        when(reportingDao.findById(organizationId, iuf)).thenReturn(expectedClassificationDTOS)
            .thenReturn(expectedClassificationDTOS);

        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, iuf);

        assertEquals(expectedIufClassificationDTO, result);
    }

    @Test
    void givenClassificationOrganizationBlankThenFailed() {
        String organizationId = "";
        String iuf = "IUF_TO_SEARCH";

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .reportingDTOList(new ArrayList<>())
                .success(false)
                .build();
        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, iuf);
        assertEquals(expectedIufClassificationDTO, result);
    }

    @Test
    void givenClassificationIufBlankThenFailed() {
        String organizationId = "10";
        String iuf = "";

        IufClassificationDTO expectedIufClassificationDTO = IufClassificationDTO.builder()
                .reportingDTOList(new ArrayList<>())
                .success(false)
                .build();
        IufClassificationDTO result = iufClassificationActivity.classify(organizationId, iuf);
        assertEquals(expectedIufClassificationDTO, result);
    }

}

