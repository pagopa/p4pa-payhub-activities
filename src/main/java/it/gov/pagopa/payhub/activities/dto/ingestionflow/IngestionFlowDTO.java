package it.gov.pagopa.payhub.activities.dto.ingestionflow;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Data Transfer Object (DTO) representing the details of an ingestion flow.
 *
 * <p>
 * This class encapsulates metadata and operational details about an ingestion flow,
 * serving as a structured data container for various application layers such as
 * service, controller, or repository layers.
 * </p>
 *
 * <h2>Purpose</h2>
 * The {@code IngestionFlowDTO} provides a standardized way to transfer and manipulate
 * information about ingestion flows, which may include details such as the flow's
 * identifier, organization, type, timestamps, status, and other relevant metadata.
 *
 * <h2>Annotations</h2>
 * <ul>
 *   <li>{@link lombok.Data} - Generates getter, setter, equals, hashCode, and toString methods.</li>
 *   <li>{@link lombok.Builder} - Enables the builder pattern for object creation.</li>
 *   <li>{@link lombok.NoArgsConstructor} - Generates a no-arguments constructor.</li>
 *   <li>{@link lombok.AllArgsConstructor} - Generates an all-arguments constructor.</li>
 * </ul>
 *
 * <h2>Fields</h2>
 * <ul>
 *   <li><strong>ingestionFlowId</strong>: Unique identifier of the ingestion flow.</li>
 *   <li><strong>version</strong>: Version number of the ingestion flow.</li>
 *   <li><strong>orgId</strong>: The {@link OrganizationDTO} associated with the ingestion flow.</li>
 *   <li><strong>PspIdentifier</strong>: Identifier of the Payment Service Provider (PSP) involved.</li>
 *   <li><strong>flowIdentifierCode</strong>: Code uniquely identifying the ingestion flow.</li>
 *   <li><strong>flowDateTime</strong>: Timestamp of when the ingestion flow occurred.</li>
 *   <li><strong>flowType</strong>: Type/category of the ingestion flow.</li>
 *   <li><strong>flowTypeCode</strong>: Code representing the type of the flow.</li>
 *   <li><strong>operatorName</strong>: Name of the operator managing the ingestion flow.</li>
 *   <li><strong>status</strong>: Current status of the ingestion flow (e.g., completed, pending).</li>
 *   <li><strong>filePathName</strong>: Full path of the file associated with the ingestion flow.</li>
 *   <li><strong>fileName</strong>: Name of the file associated with the ingestion flow.</li>
 *   <li><strong>downloadedFileSize</strong>: Size of the file in bytes.</li>
 *   <li><strong>requestTokenCode</strong>: Token code for the request associated with the ingestion flow.</li>
 *   <li><strong>creationDate</strong>: Timestamp indicating when the ingestion flow was created.</li>
 *   <li><strong>lastChangeDate</strong>: Timestamp of the last modification to the ingestion flow.</li>
 *   <li><strong>fileSourceCode</strong>: Code indicating the source of the file.</li>
 *   <li><strong>descriptionFileNameScraps</strong>: Additional description or scraps from the file name.</li>
 *   <li><strong>errorCode</strong>: Code representing any errors encountered during the ingestion process.</li>
 *   <li><strong>TotalRowsNumber</strong>: Total number of rows in the file.</li>
 *   <li><strong>numberLinesImportedCorrectly</strong>: Number of rows successfully imported.</li>
 * </ul>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Comprehensive metadata representation for ingestion flows.</li>
 *   <li>Integration with other DTOs, such as {@link OrganizationDTO}, for structured relationships.</li>
 *   <li>Leveraging Lombok annotations for concise and maintainable code.</li>
 * </ul>
 *
 * @see OrganizationDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionFlowDTO {
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

