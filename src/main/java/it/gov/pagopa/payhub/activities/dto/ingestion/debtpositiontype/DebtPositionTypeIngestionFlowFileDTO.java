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

    @CsvBindByName(column = "brokerCf", required = true)
    private String brokerCf;

    @CsvBindByName(column = "debtPositionTypeCode", required = true)
    private String debtPositionTypeCode;

    @CsvBindByName(column = "description", required = true)
    private String description;

    @CsvBindByName(column = "orgType", required = true)
    private String orgType;

    @CsvBindByName(column = "macroArea", required = true)
    private String macroArea;

    @CsvBindByName(column = "serviceType", required = true)
    private String serviceType;

    @CsvBindByName(column = "collectingReason", required = true)
    private String collectingReason;

    @CsvBindByName(column = "taxonomyCode", required = true)
    private String taxonomyCode;

    @CsvBindByName(column = "flagAnonymousFiscalCode", required = true)
    private Boolean flagAnonymousFiscalCode;

    @CsvBindByName(column = "flagMandatoryDueDate", required = true)
    private Boolean flagMandatoryDueDate;

    @CsvBindByName(column = "flagNotifyIo", required = true)
    private Boolean flagNotifyIo;

    @CsvBindByName(column = "ioTemplateMessage")
    private String ioTemplateMessage;

    @CsvBindByName(column = "ioTemplateSubject", required = true)
    private String ioTemplateSubject;

}

