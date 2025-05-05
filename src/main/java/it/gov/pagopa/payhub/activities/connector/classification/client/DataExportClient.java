package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Lazy
@Slf4j
@Service
public class DataExportClient {
    private final ClassificationApisHolder classificationApisHolder;

    public DataExportClient(ClassificationApisHolder classificationApisHolder) {
        this.classificationApisHolder = classificationApisHolder;
    }

    public PagedClassificationView getPagedClassificationView(String accessToken,
                                                              Long organizationId,
                                                              String operatorExternalUserId,
                                                              ClassificationsExportFileFilter classificationsExportFileFilter,
                                                              Integer page,
                                                              Integer size,
                                                              List<String> sort){

        LocalDate lastClassificationDateFrom = classificationsExportFileFilter.getLastClassificationDate() != null ? classificationsExportFileFilter.getLastClassificationDate().getFrom() : null;
        LocalDate lastClassificationDateTo = classificationsExportFileFilter.getLastClassificationDate() != null ? classificationsExportFileFilter.getLastClassificationDate().getTo() : null;
        OffsetDateTimeIntervalFilter payDate =  Utilities.toOffsetDateTimeIntervalFilterForDayBounds(classificationsExportFileFilter.getPayDate());
        OffsetDateTimeIntervalFilter paymentDate = Utilities.toOffsetDateTimeIntervalFilterForDayBounds(classificationsExportFileFilter.getPaymentDate());
        LocalDate regulationDateFrom = classificationsExportFileFilter.getRegulationDate() != null ? classificationsExportFileFilter.getRegulationDate().getFrom() : null;
        LocalDate regulationDateTo = classificationsExportFileFilter.getRegulationDate() != null ? classificationsExportFileFilter.getRegulationDate().getTo() : null;
        LocalDate billDateFrom = classificationsExportFileFilter.getBillDate() != null ? classificationsExportFileFilter.getBillDate().getFrom() : null;
        LocalDate billDateTo = classificationsExportFileFilter.getBillDate() != null ? classificationsExportFileFilter.getBillDate().getTo() : null;
        LocalDate regionValueDateFrom = classificationsExportFileFilter.getRegionValueDate() != null ? classificationsExportFileFilter.getRegionValueDate().getFrom() : null;
        LocalDate regionValueDateTo = classificationsExportFileFilter.getRegionValueDate() != null ? classificationsExportFileFilter.getRegionValueDate().getTo() : null;

        ClassificationsEnum label = classificationsExportFileFilter.getLabel() != null ? ClassificationsEnum.fromValue(classificationsExportFileFilter.getLabel().getValue()) : null;

        return classificationApisHolder.getDataExportsApi(accessToken).exportClassifications(
                organizationId,
                operatorExternalUserId,
                label,
                lastClassificationDateFrom,
                lastClassificationDateTo,
                classificationsExportFileFilter.getIuf(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuv(),
                classificationsExportFileFilter.getIur(),
                payDate.getFrom(),
                payDate.getTo(),
                paymentDate.getFrom(),
                paymentDate.getTo(),
                regulationDateFrom,
                regulationDateTo,
                billDateFrom,
                billDateTo,
                regionValueDateFrom,
                regionValueDateTo,
                classificationsExportFileFilter.getRegulationUniqueIdentifier(),
                classificationsExportFileFilter.getAccountRegistryCode(),
                classificationsExportFileFilter.getBillAmountCents(),
                classificationsExportFileFilter.getRemittanceInformation(),
                classificationsExportFileFilter.getPspCompanyName(),
                classificationsExportFileFilter.getPspLastName(),
                page,
                size,
                sort);
    }

    public PagedFullClassificationView getPagedFullClassificationView(String accessToken,
                                                                  Long organizationId,
                                                                  String operatorExternalUserId,
                                                                  ClassificationsExportFileFilter classificationsExportFileFilter,
                                                                  Integer page,
                                                                  Integer size,
                                                                  List<String> sort) {

        LocalDate lastClassificationDateFrom = classificationsExportFileFilter.getLastClassificationDate() != null ? classificationsExportFileFilter.getLastClassificationDate().getFrom() : null;
        LocalDate lastClassificationDateTo = classificationsExportFileFilter.getLastClassificationDate() != null ? classificationsExportFileFilter.getLastClassificationDate().getTo() : null;
        OffsetDateTimeIntervalFilter payDate = Utilities.toOffsetDateTimeIntervalFilterForDayBounds(classificationsExportFileFilter.getPayDate());
        OffsetDateTimeIntervalFilter paymentDate = Utilities.toOffsetDateTimeIntervalFilterForDayBounds(classificationsExportFileFilter.getPaymentDate());
        LocalDate regulationDateFrom = classificationsExportFileFilter.getRegulationDate() != null ? classificationsExportFileFilter.getRegulationDate().getFrom() : null;
        LocalDate regulationDateTo = classificationsExportFileFilter.getRegulationDate() != null ? classificationsExportFileFilter.getRegulationDate().getTo() : null;
        LocalDate billDateFrom = classificationsExportFileFilter.getBillDate() != null ? classificationsExportFileFilter.getBillDate().getFrom() : null;
        LocalDate billDateTo = classificationsExportFileFilter.getBillDate() != null ? classificationsExportFileFilter.getBillDate().getTo() : null;
        LocalDate regionValueDateFrom = classificationsExportFileFilter.getRegionValueDate() != null ? classificationsExportFileFilter.getRegionValueDate().getFrom() : null;
        LocalDate regionValueDateTo = classificationsExportFileFilter.getRegionValueDate() != null ? classificationsExportFileFilter.getRegionValueDate().getTo() : null;

        ClassificationsEnum label = classificationsExportFileFilter.getLabel() != null ? ClassificationsEnum.fromValue(classificationsExportFileFilter.getLabel().getValue()) : null;

        return classificationApisHolder.getDataExportsApi(accessToken).exportFullClassifications(
                organizationId,
                operatorExternalUserId,
                label,
                lastClassificationDateFrom,
                lastClassificationDateTo,
                classificationsExportFileFilter.getIuf(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuv(),
                classificationsExportFileFilter.getIur(),
                payDate.getFrom(),
                payDate.getTo(),
                paymentDate.getFrom(),
                paymentDate.getTo(),
                regulationDateFrom,
                regulationDateTo,
                billDateFrom,
                billDateTo,
                regionValueDateFrom,
                regionValueDateTo,
                classificationsExportFileFilter.getRegulationUniqueIdentifier(),
                classificationsExportFileFilter.getAccountRegistryCode(),
                classificationsExportFileFilter.getBillAmountCents(),
                classificationsExportFileFilter.getRemittanceInformation(),
                classificationsExportFileFilter.getPspCompanyName(),
                classificationsExportFileFilter.getPspLastName(),
                page,
                size,
                sort);
    }
}
