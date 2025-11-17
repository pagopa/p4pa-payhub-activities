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
import java.util.Set;
import java.util.stream.Collectors;

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
                classificationsExportFileFilter.getIufs(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuvs(),
                classificationsExportFileFilter.getIurs(),
                params.lastClassificationDateFrom,
                params.lastClassificationDateTo,
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
                classificationsExportFileFilter.getDebtPositionTypeOrgCodes(),
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
                classificationsExportFileFilter.getIufs(),
                classificationsExportFileFilter.getIud(),
                classificationsExportFileFilter.getIuvs(),
                classificationsExportFileFilter.getIurs(),
                params.lastClassificationDateFrom,
                params.lastClassificationDateTo,
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
                classificationsExportFileFilter.getDebtPositionTypeOrgCodes(),
                page,
                size,
                sort);
    }

    private FilterParams extractFilterParams(ClassificationsExportFileFilter filter) {
        LocalDate lastClassificationDateFrom = filter.getLastClassificationDate() != null ? filter.getLastClassificationDate().getFrom() : null;
        LocalDate lastClassificationDateTo = filter.getLastClassificationDate() != null ? filter.getLastClassificationDate().getTo() : null;
        LocalDate payDateFrom = filter.getPayDate() != null ? filter.getPayDate().getFrom() : null;
        LocalDate payDateTo = filter.getPayDate() != null ? filter.getPayDate().getTo() : null;
        OffsetDateTimeIntervalFilter paymentDate = Utilities.toRangeClosedOffsetDateTimeIntervalFilter(filter.getPaymentDate());
        LocalDate regulationDateFrom = filter.getRegulationDate() != null ? filter.getRegulationDate().getFrom() : null;
        LocalDate regulationDateTo = filter.getRegulationDate() != null ? filter.getRegulationDate().getTo() : null;
        LocalDate billDateFrom = filter.getBillDate() != null ? filter.getBillDate().getFrom() : null;
        LocalDate billDateTo = filter.getBillDate() != null ? filter.getBillDate().getTo() : null;
        LocalDate regionValueDateFrom = filter.getRegionValueDate() != null ? filter.getRegionValueDate().getFrom() : null;
        LocalDate regionValueDateTo = filter.getRegionValueDate() != null ? filter.getRegionValueDate().getTo() : null;
        Set<ClassificationsEnum> labels = filter.getLabel() != null ? filter.getLabel().stream().map( l -> ClassificationsEnum.fromValue(l.getValue())).collect(Collectors.toSet()) : null;
        return new FilterParams(lastClassificationDateFrom, lastClassificationDateTo,payDateFrom, payDateTo, paymentDate.getFrom(), paymentDate.getTo(), regulationDateFrom, regulationDateTo, billDateFrom, billDateTo, regionValueDateFrom, regionValueDateTo, labels);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FilterParams {
        LocalDate lastClassificationDateFrom;
        LocalDate lastClassificationDateTo;
        LocalDate payDateFrom;
        LocalDate payDateTo;
        OffsetDateTime paymentDateFrom;
        OffsetDateTime paymentDateTo;
        LocalDate regulationDateFrom;
        LocalDate regulationDateTo;
        LocalDate billDateFrom;
        LocalDate billDateTo;
        LocalDate regionValueDateFrom;
        LocalDate regionValueDateTo;
        Set<ClassificationsEnum> label;
    }
}
