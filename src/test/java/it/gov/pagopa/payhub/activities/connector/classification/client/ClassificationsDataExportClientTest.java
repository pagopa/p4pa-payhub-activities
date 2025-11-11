package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.client.generated.DataExportsApi;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ClassificationsDataExportClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;
    @Mock
    private DataExportsApi dataExportsApiMock;

    private PodamFactory podamFactory;
    private ClassificationsDataExportClient classificationsDataExportClient;

    @BeforeEach
    void setUp() {
        classificationsDataExportClient = new ClassificationsDataExportClient(classificationApisHolderMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
               classificationApisHolderMock,
               dataExportsApiMock
        );
    }

    @Test
    void givenParameters_WhenGetPagedClassificationView_ThenReturnPagedClassificationView() {
        //given
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        String accessToken = "accessToken";
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);

        PagedClassificationView pagedClassificationView = podamFactory.manufacturePojo(PagedClassificationView.class);

        Mockito.when(classificationApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportClassifications(
               organizationId,
                operatorExternalUserId,
                classificationsExportFileFilter.getLabel().stream().map(l -> ClassificationsEnum.fromValue(l.getValue())).collect(Collectors.toSet()),
                classificationsExportFileFilter.getLastClassificationDate().getFrom(),
                classificationsExportFileFilter.getLastClassificationDate().getTo(),
                classificationsExportFileFilter.getIufs(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuvs(),
                classificationsExportFileFilter.getIurs(),
                classificationsExportFileFilter.getPayDate().getFrom(),
                classificationsExportFileFilter.getPayDate().getTo(),
                Utilities.toOffsetDateTimeStartOfTheDay(classificationsExportFileFilter.getPaymentDate().getFrom()),
                Utilities.toOffsetDateTimeEndOfTheDay(classificationsExportFileFilter.getPayDate().getTo()),
                classificationsExportFileFilter.getRegulationDate().getFrom(),
                classificationsExportFileFilter.getRegulationDate().getTo(),
                classificationsExportFileFilter.getBillDate().getFrom(),
                classificationsExportFileFilter.getBillDate().getTo(),
                classificationsExportFileFilter.getRegionValueDate().getFrom(),
                classificationsExportFileFilter.getRegionValueDate().getTo(),
                classificationsExportFileFilter.getRegulationUniqueIdentifier(),
                classificationsExportFileFilter.getAccountRegistryCode(),
                classificationsExportFileFilter.getBillAmountCents(),
                classificationsExportFileFilter.getRemittanceInformation(),
                classificationsExportFileFilter.getPspCompanyName(),
                classificationsExportFileFilter.getPspLastName(),
                classificationsExportFileFilter.getDebtPositionTypeOrgCodes(),
                0,
                0,
                List.of("classificationId"))).thenReturn(pagedClassificationView);
        //when
        PagedClassificationView result = classificationsDataExportClient.getPagedClassificationView(accessToken, organizationId, operatorExternalUserId, classificationsExportFileFilter, 0, 0, List.of("classificationId"));
        //then
        assertNotNull(result);
        assertEquals(pagedClassificationView, result);
    }

    @Test
    void givenParameters_WhenGetPagedFullClassificationView_ThenReturnPagedFullClassificationView() {
        //given
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        String accessToken = "accessToken";
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);

        PagedFullClassificationView pagedFullClassificationView = podamFactory.manufacturePojo(PagedFullClassificationView.class);

        Mockito.when(classificationApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportFullClassifications(
                organizationId,
                operatorExternalUserId,
                classificationsExportFileFilter.getLabel().stream().map(l -> ClassificationsEnum.fromValue(l.getValue())).collect(Collectors.toSet()),
                classificationsExportFileFilter.getLastClassificationDate().getFrom(),
                classificationsExportFileFilter.getLastClassificationDate().getTo(),
                classificationsExportFileFilter.getIufs(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuvs(),
                classificationsExportFileFilter.getIurs(),
                classificationsExportFileFilter.getPayDate().getFrom(),
                classificationsExportFileFilter.getPayDate().getTo(),
                Utilities.toOffsetDateTimeStartOfTheDay(classificationsExportFileFilter.getPaymentDate().getFrom()),
                Utilities.toOffsetDateTimeEndOfTheDay(classificationsExportFileFilter.getPayDate().getTo()),
                classificationsExportFileFilter.getRegulationDate().getFrom(),
                classificationsExportFileFilter.getRegulationDate().getTo(),
                classificationsExportFileFilter.getBillDate().getFrom(),
                classificationsExportFileFilter.getBillDate().getTo(),
                classificationsExportFileFilter.getRegionValueDate().getFrom(),
                classificationsExportFileFilter.getRegionValueDate().getTo(),
                classificationsExportFileFilter.getRegulationUniqueIdentifier(),
                classificationsExportFileFilter.getAccountRegistryCode(),
                classificationsExportFileFilter.getBillAmountCents(),
                classificationsExportFileFilter.getRemittanceInformation(),
                classificationsExportFileFilter.getPspCompanyName(),
                classificationsExportFileFilter.getPspLastName(),
                classificationsExportFileFilter.getDebtPositionTypeOrgCodes(),
                0,
                0,
                List.of("classificationId"))).thenReturn(pagedFullClassificationView);
        //when
        PagedFullClassificationView result = classificationsDataExportClient.getPagedFullClassificationView(accessToken, organizationId, operatorExternalUserId, classificationsExportFileFilter, 0, 0, List.of("classificationId"));
        //then
        assertNotNull(result);
        assertEquals(pagedFullClassificationView, result);
    }

    @Test
    void givenSomeNullParameters_WhenGetPagedClassificationView_ThenReturnPagedClassificationView() {
        //given
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        String accessToken = "accessToken";
        ClassificationsExportFileFilter classificationsExportFileFilter = podamFactory.manufacturePojo(ClassificationsExportFileFilter.class);
        classificationsExportFileFilter.setLabel(null);
        classificationsExportFileFilter.setLastClassificationDate(null);
        classificationsExportFileFilter.setRegulationDate(null);
        classificationsExportFileFilter.setBillDate(null);
        classificationsExportFileFilter.setRegionValueDate(null);
        PagedClassificationView pagedClassificationView = podamFactory.manufacturePojo(PagedClassificationView.class);

        Mockito.when(classificationApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportClassifications(
                organizationId,
                operatorExternalUserId,
                null,
                null,
                null,
                classificationsExportFileFilter.getIufs(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuvs(),
                classificationsExportFileFilter.getIurs(),
                classificationsExportFileFilter.getPayDate().getFrom(),
                classificationsExportFileFilter.getPayDate().getTo(),
                Utilities.toOffsetDateTimeStartOfTheDay(classificationsExportFileFilter.getPaymentDate().getFrom()),
                Utilities.toOffsetDateTimeEndOfTheDay(classificationsExportFileFilter.getPayDate().getTo()),
                null,
                null,
                null,
                null,
                null,
                null,
                classificationsExportFileFilter.getRegulationUniqueIdentifier(),
                classificationsExportFileFilter.getAccountRegistryCode(),
                classificationsExportFileFilter.getBillAmountCents(),
                classificationsExportFileFilter.getRemittanceInformation(),
                classificationsExportFileFilter.getPspCompanyName(),
                classificationsExportFileFilter.getPspLastName(),
                classificationsExportFileFilter.getDebtPositionTypeOrgCodes(),
                0,
                0,
                List.of("classificationId"))).thenReturn(pagedClassificationView);
        //when
        PagedClassificationView result = classificationsDataExportClient.getPagedClassificationView(accessToken, organizationId, operatorExternalUserId, classificationsExportFileFilter, 0, 0, List.of("classificationId"));
        //then
        assertNotNull(result);
        assertEquals(pagedClassificationView, result);
    }
}