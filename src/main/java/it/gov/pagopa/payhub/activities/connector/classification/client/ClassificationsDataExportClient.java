package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Slf4j
@Service
public class ClassificationsDataExportClient {
    private final ClassificationApisHolder classificationApisHolder;

    public ClassificationsDataExportClient(ClassificationApisHolder classificationApisHolder) {
        this.classificationApisHolder = classificationApisHolder;
    }

    public PagedClassificationView getPagedClassificationView(String accessToken,
                                                              Long organizationId,
                                                              String operatorExternalUserId,
                                                              ClassificationsExportFileFilter classificationsExportFileFilter,
                                                              Integer page,
                                                              Integer size,
                                                              List<String> sort) {
        FilterParams params = extractFilterParams(classificationsExportFileFilter);
        return classificationApisHolder.getDataExportsApi(accessToken).exportClassifications(
                organizationId,
                operatorExternalUserId,
                params.label,
                params.lastClassificationDateFrom,
                params.lastClassificationDateTo,
                classificationsExportFileFilter.getIuf(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuv(),
                classificationsExportFileFilter.getIur(),
                params.payDateFrom,
                params.payDateTo,
                params.paymentDateFrom,
                params.paymentDateTo,
                params.regulationDateFrom,
                params.regulationDateTo,
                params.billDateFrom,
                params.billDateTo,
                params.regionValueDateFrom,
                params.regionValueDateTo,
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
        FilterParams params = extractFilterParams(classificationsExportFileFilter);
        return classificationApisHolder.getDataExportsApi(accessToken).exportFullClassifications(
                organizationId,
                operatorExternalUserId,
                params.label,
                params.lastClassificationDateFrom,
                params.lastClassificationDateTo,
                classificationsExportFileFilter.getIuf(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuv(),
                classificationsExportFileFilter.getIur(),
                params.payDateFrom,
                params.payDateTo,
                params.paymentDateFrom,
                params.paymentDateTo,
                params.regulationDateFrom,
                params.regulationDateTo,
                params.billDateFrom,
                params.billDateTo,
                params.regionValueDateFrom,
                params.regionValueDateTo,
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

    private FilterParams extractFilterParams(ClassificationsExportFileFilter filter) {
        LocalDate lastClassificationDateFrom = filter.getLastClassificationDate() != null ? filter.getLastClassificationDate().getFrom() : null;
        LocalDate lastClassificationDateTo = filter.getLastClassificationDate() != null ? filter.getLastClassificationDate().getTo() : null;
        OffsetDateTimeIntervalFilter payDate = Utilities.toOffsetDateTimeIntervalFilterForDayBounds(filter.getPayDate());
        OffsetDateTimeIntervalFilter paymentDate = Utilities.toOffsetDateTimeIntervalFilterForDayBounds(filter.getPaymentDate());
        LocalDate regulationDateFrom = filter.getRegulationDate() != null ? filter.getRegulationDate().getFrom() : null;
        LocalDate regulationDateTo = filter.getRegulationDate() != null ? filter.getRegulationDate().getTo() : null;
        LocalDate billDateFrom = filter.getBillDate() != null ? filter.getBillDate().getFrom() : null;
        LocalDate billDateTo = filter.getBillDate() != null ? filter.getBillDate().getTo() : null;
        LocalDate regionValueDateFrom = filter.getRegionValueDate() != null ? filter.getRegionValueDate().getFrom() : null;
        LocalDate regionValueDateTo = filter.getRegionValueDate() != null ? filter.getRegionValueDate().getTo() : null;
        ClassificationsEnum label = filter.getLabel() != null ? ClassificationsEnum.fromValue(filter.getLabel().getValue()) : null;

        return new FilterParams(lastClassificationDateFrom, lastClassificationDateTo, payDate.getFrom(), payDate.getTo(), paymentDate.getFrom(), paymentDate.getTo(), regulationDateFrom, regulationDateTo, billDateFrom, billDateTo, regionValueDateFrom, regionValueDateTo, label);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FilterParams {
        LocalDate lastClassificationDateFrom;
        LocalDate lastClassificationDateTo;
        OffsetDateTime payDateFrom;
        OffsetDateTime payDateTo;
        OffsetDateTime paymentDateFrom;
        OffsetDateTime paymentDateTo;
        LocalDate regulationDateFrom;
        LocalDate regulationDateTo;
        LocalDate billDateFrom;
        LocalDate billDateTo;
        LocalDate regionValueDateFrom;
        LocalDate regionValueDateTo;
        ClassificationsEnum label;
    }
}
