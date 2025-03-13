package it.gov.pagopa.payhub.activities.connector.debtposition;


import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DataExportClient;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentsRequestFilterDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class DataExportServiceImpl implements DataExportService{

    private final DataExportClient dataExportClient;
    private final AuthnService authnService;

    public DataExportServiceImpl(DataExportClient dataExportClient, AuthnService authnService) {
        this.dataExportClient = dataExportClient;
        this.authnService = authnService;
    }

    @Override
    public PagedInstallmentsPaidView exportPaidInstallments(PaidInstallmentsRequestFilterDTO paidInstallmentsRequestFilterDTO, Integer page, Integer size, List<String> sort) {
        return dataExportClient.getExportPaidInstallments(authnService.getAccessToken(), paidInstallmentsRequestFilterDTO, page, size, sort);
    }

}
