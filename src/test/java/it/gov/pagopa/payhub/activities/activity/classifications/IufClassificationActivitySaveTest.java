package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker.buildPaymentsReportingDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker.buildTreasuryDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivitySaveTest {

    @Mock
    private PaymentsReportingDao paymentsReportingDao;
    @Mock
    private TreasuryDao treasuryDao;
    @Mock
    private ClassificationDao classificationDao;

    private IufClassificationActivity iufClassificationActivity;

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingDao, treasuryDao, classificationDao);
    }

    @Test
    void saveClassificationSuccess() {
        PaymentsReportingDTO expectedReportingDTO = buildPaymentsReportingDTO();
        List<PaymentsReportingDTO> expectedPaymentsReportingDTOS = new ArrayList<>();
        expectedPaymentsReportingDTOS.add(expectedReportingDTO);

        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        List<TreasuryDTO> expectedTreasuryDTOS = new ArrayList<>();
        expectedTreasuryDTOS.add(expectedTreasuryDTO);

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .paymentsReportingDTOS(expectedPaymentsReportingDTOS)
                        .success(true)
                        .build();
        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();
        Long expectedOrganizationId = expectedReportingDTO.getOrganizationId();

        when(paymentsReportingDao.findByOrganizationIdFlowIdentifierCode(expectedOrganizationId, flowIdentifierCode))
                .thenReturn(expectedPaymentsReportingDTOS);

        when(treasuryDao.searchByIuf(flowIdentifierCode))
                .thenReturn(expectedTreasuryDTOS);

        IufClassificationActivityResult iufClassificationActivityResult = iufClassificationActivity.classify(expectedOrganizationId, flowIdentifierCode);
        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }

    @Test
    void saveClassificationNoReportingSuccess() {
        Long expectedOrganizationId = 1L;
        List<PaymentsReportingDTO> expectedPaymentsReportingDTOS = new ArrayList<>();

        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        List<TreasuryDTO> expectedTreasuryDTOS = new ArrayList<>();
        expectedTreasuryDTOS.add(expectedTreasuryDTO);

        IufClassificationActivityResult expectedIufClassificationActivityResult =
                IufClassificationActivityResult
                        .builder()
                        .paymentsReportingDTOS(expectedPaymentsReportingDTOS)
                        .success(true)
                        .build();
        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();

        when(treasuryDao.searchByIuf(flowIdentifierCode))
                .thenReturn(expectedTreasuryDTOS);

        IufClassificationActivityResult iufClassificationActivityResult = iufClassificationActivity.classify(expectedOrganizationId, flowIdentifierCode);
        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }

}
