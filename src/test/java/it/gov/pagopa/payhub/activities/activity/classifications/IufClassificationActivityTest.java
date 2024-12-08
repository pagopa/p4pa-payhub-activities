package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.activity.debtposition.AuthorizeOperatorOnDebtPositionTypeActivity;
import it.gov.pagopa.payhub.activities.activity.debtposition.AuthorizeOperatorOnDebtPositionTypeActivityImpl;
import it.gov.pagopa.payhub.activities.dao.DebtPositionTypeOrgDao;
import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        ClassificationDTO firstClassification =
                ClassificationDTO.builder()
                    .iuv("IUV")
                    .orgId(Long.valueOf(organizationId))
                    .receiptId(2L)
                    .amount(10L)
                    .transferId(123L)
                    .transferIndex(1L)
                    .build();

        List<ClassificationDTO> expectedClassificationDTOS = new ArrayList<>();
        expectedClassificationDTOS.add(firstClassification);

        when(reportingDao.findById(organizationId, iuf)).thenReturn(expectedClassificationDTOS)
            .thenReturn(expectedClassificationDTOS);

        List<ClassificationDTO> result = iufClassificationActivity.classify(organizationId, iuf);

        assertEquals(expectedClassificationDTOS, result);
    }

}

