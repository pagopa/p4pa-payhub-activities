package it.gov.pagopa.payhub.activities.connector.debtposition;


import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DataExportClient;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class DebtPositionsDataExportServiceImpl implements DebtPositionsDataExportService {

    private final DataExportClient dataExportClient;
    private final AuthnService authnService;

    public DebtPositionsDataExportServiceImpl(DataExportClient dataExportClient, AuthnService authnService) {
        this.dataExportClient = dataExportClient;
        this.authnService = authnService;
    }

    @Override
    public PagedInstallmentsPaidView exportPaidInstallments(Long organizationId, String operatorExternalUserId, PaidExportFileFilter paidExportFileFilter, Integer page, Integer size, List<String> sort) {
        return dataExportClient.getExportPaidInstallments(authnService.getAccessToken(),organizationId, operatorExternalUserId, paidExportFileFilter, page, size, sort);
    }

    @Override
    public PagedReceiptsArchivingView exportReceiptsArchivingView(Long organizationId, String operatorExternalUserId, ReceiptsArchivingExportFileFilter receiptsArchivingExportFileFilter, Integer page, Integer size, List<String> sort) {
        return dataExportClient.getExportReceiptsArchivingView(authnService.getAccessToken(), organizationId, operatorExternalUserId, receiptsArchivingExportFileFilter, page, size, sort);
    }

}
