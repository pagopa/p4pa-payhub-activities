package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivitySaveTest {

    @Mock
    private PaymentsReportingDao paymentsReportingDao;
    @Mock
    TreasuryDao treasuryDao;
    @Mock
    private ClassifyDao classifyDao;

    private IufClassificationActivity iufClassificationActivity;

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsReportingDao, treasuryDao, classifyDao);
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

        IufClassificationActivityResult iufClassificationActivityResult = iufClassificationActivity.save(expectedOrganizationId, flowIdentifierCode);
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

        IufClassificationActivityResult iufClassificationActivityResult = iufClassificationActivity.save(expectedOrganizationId, flowIdentifierCode);
        assertEquals(iufClassificationActivityResult,expectedIufClassificationActivityResult);
    }


    @Test
    void saveClassificationIufNullFailed() {
        PaymentsReportingDTO expectedReportingDTO = buildPaymentsReportingDTO();
        Long expectedOrganizationId = expectedReportingDTO.getOrganizationId();
        PaymentsClassificationSaveException paymentsClassificationSaveException =
                assertThrows(PaymentsClassificationSaveException.class, () ->
                        iufClassificationActivity.save(expectedOrganizationId, null));
        assertEquals("iuf may be not null or blank", paymentsClassificationSaveException.getMessage());
    }

    @Test
    void saveClassificationIufBlankFailed() {
        PaymentsReportingDTO expectedReportingDTO = buildPaymentsReportingDTO();
        Long expectedOrganizationId = expectedReportingDTO.getOrganizationId();
        PaymentsClassificationSaveException paymentsClassificationSaveException =
                assertThrows(PaymentsClassificationSaveException.class, () ->
                        iufClassificationActivity.save(expectedOrganizationId, ""));
        assertEquals("iuf may be not null or blank", paymentsClassificationSaveException.getMessage());
    }

    @Test
    void saveClassificationOrganizationIdNullFailed() {
        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();

        PaymentsClassificationSaveException paymentsClassificationSaveException =
                assertThrows(PaymentsClassificationSaveException.class, () ->
                        iufClassificationActivity.save(null, flowIdentifierCode));
        assertEquals("organization id may be not null or zero", paymentsClassificationSaveException.getMessage());
    }
    @Test
    void saveClassificationOrganizationIdZeroFailed() {
        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();

        PaymentsClassificationSaveException paymentsClassificationSaveException =
                assertThrows(PaymentsClassificationSaveException.class, () ->
                        iufClassificationActivity.save(0L, flowIdentifierCode));
        assertEquals("organization id may be not null or zero", paymentsClassificationSaveException.getMessage());
    }

}

