package it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DebtPositionTypeIngestionFlowFileDTO {

    @CsvBindByName(column = "broker_cf", required = true)
    private String brokerCf;

    @CsvBindByName(column = "debt_position_type_code", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "description", required = true)
    private String description;

    @CsvBindByName(column = "org_type", required = true)
    private String orgType;

    @CsvBindByName(column = "macro_area", required = true)
    private String macroArea;

    @CsvBindByName(column = "service_type", required = true)
    private String serviceType;

    @CsvBindByName(column = "collecting_reason", required = true)
    private String collectingReason;

    @CsvBindByName(column = "taxonomy_code", required = true)
    private String taxonomyCode;

    @CsvBindByName(column = "flag_anonymous_fiscal_code", required = true)
    private Boolean flagAnonymousFiscalCode;

    @CsvBindByName(column = "flag_mandatory_due_date", required = true)
    private Boolean flagMandatoryDueDate;

    @CsvBindByName(column = "flag_notify_io", required = true)
    private Boolean flagNotifyIo;

    @CsvBindByName(column = "io_template_message")
    private String ioTemplateMessage;

    @CsvBindByName(column = "io_template_subject", required = true)
    private String ioTemplateSubject;

}

