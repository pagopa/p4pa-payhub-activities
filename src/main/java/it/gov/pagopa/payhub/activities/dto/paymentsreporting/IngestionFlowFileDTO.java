package it.gov.pagopa.payhub.activities.dto.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Data Transfer Object (DTO) representing the details of an ingestion flow.
 *
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionFlowFileDTO {
	private Long ingestionFlowId;
	private int version;
	private OrganizationDTO orgId;
	private String PspIdentifier;
	private String flowIdentifierCode;
	private Timestamp flowDateTime;
	private String flowType;
	private String flowTypeCode;
	private String operatorName;
	private String status;
	private String filePathName;
	private String fileName;
	private Long downloadedFileSize;
	private String requestTokenCode;
	private Timestamp creationDate;
	private Timestamp lastChangeDate;
	private String fileSourceCode;
	private String descriptionFileNameScraps;
	private String errorCode;
	private Long TotalRowsNumber;
	private Long numberLinesImportedCorrectly;
}

