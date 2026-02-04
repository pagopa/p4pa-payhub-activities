package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Service
@Lazy
@Slf4j
public class TreasuryXlsProcessingService extends IngestionFlowProcessingService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryXlsErrorDTO> {

	private final TreasuryXlsMapper treasuryXlsMapper;
	private final TreasuryService treasuryService;
	private final FileExceptionHandlerService fileExceptionHandlerService;

	public TreasuryXlsProcessingService(
            @Value("${ingestion-flow-files.treasuries.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            TreasuryXlsMapper treasuryXlsMapper,
            TreasuryService treasuryService,
            TreasuryXlsErrorsArchiverService treasuryXlsErrorsArchiverService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
		super(maxConcurrentProcessingRows, treasuryXlsErrorsArchiverService, organizationService, fileExceptionHandlerService);
		this.treasuryXlsMapper = treasuryXlsMapper;
		this.treasuryService = treasuryService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    @Override
    protected String getSequencingId(TreasuryXlsIngestionFlowFileDTO row) {
        return TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
    }

    @Override
	protected List<TreasuryXlsErrorDTO> consumeRow(long lineNumber,
                              TreasuryXlsIngestionFlowFileDTO row,
                              TreasuryIufIngestionFlowFileResult ingestionFlowFileResult,
                              IngestionFlowFile ingestionFlowFile) {
		String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
		String rowIuf = TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
		LocalDate billDate = row.getBillDate();

		try {
			TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFileResult.getOrganizationId(), rowIuf);

			if(existingTreasury != null) {
				boolean treasuryNotMatch = !existingTreasury.getBillCode().equals(TreasuryUtils.generateBillCode(rowIuf)) || !existingTreasury.getBillYear().equals(String.valueOf(billDate.getYear()));
				if (treasuryNotMatch) {
					log.error("IUF {} already associated to another treasury for organization with IPA code {}", rowIuf, ipa);
					TreasuryXlsErrorDTO error = new TreasuryXlsErrorDTO(
							ingestionFlowFile.getFileName(),
							rowIuf,
							lineNumber,
							FileErrorCode.IUF_ALREADY_ASSOCIATED.name(),
							FileErrorCode.IUF_ALREADY_ASSOCIATED.format(rowIuf, ipa));
                    return List.of(error);
				}
			}

			Treasury treasury = treasuryService.insert(
					treasuryXlsMapper.map(row, ingestionFlowFile));

			ingestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
			return Collections.emptyList();
		} catch (Exception e) {
			log.error("Error processing treasury csv with iuf {}: {}",
					rowIuf,
					e.getMessage());
			FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());

			TreasuryXlsErrorDTO error = new TreasuryXlsErrorDTO(
					ingestionFlowFile.getFileName(),
					rowIuf,
					lineNumber, errorDetails.getErrorCode(), errorDetails.getErrorMessage());
            return List.of(error);
		}
	}

	@Override
	protected TreasuryXlsErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
		return TreasuryXlsErrorDTO.builder()
				.fileName(fileName)
				.rowNumber(lineNumber)
				.errorCode(errorCode)
				.errorMessage(message)
				.build();
	}

	public TreasuryIufIngestionFlowFileResult processTreasuryXls(
			Iterator<TreasuryXlsIngestionFlowFileDTO> iterator,
			IngestionFlowFile ingestionFlowFile,
			Path workingDirectory) {
		List<TreasuryXlsErrorDTO> errorList = new ArrayList<>();
		TreasuryIufIngestionFlowFileResult ingestionFlowFileResult = new TreasuryIufIngestionFlowFileResult();
		ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
		ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());
		ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());

		process(iterator, new ArrayList<>(), ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
		return ingestionFlowFileResult;
	}

}
