package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class TreasuryXlsProcessingService extends IngestionFlowProcessingService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryXlsErrorDTO> {

	private final TreasuryXlsMapper treasuryXlsMapper;
	private final TreasuryService treasuryService;

	public TreasuryXlsProcessingService(
			TreasuryXlsMapper treasuryXlsMapper,
			TreasuryService treasuryService,
			TreasuryXlsErrorsArchiverService treasuryXlsErrorsArchiverService,
			OrganizationService organizationService) {
		super(treasuryXlsErrorsArchiverService, organizationService);
		this.treasuryXlsMapper = treasuryXlsMapper;
		this.treasuryService = treasuryService;
	}

	@Override
	protected boolean consumeRow(long lineNumber, TreasuryXlsIngestionFlowFileDTO row, TreasuryIufIngestionFlowFileResult ingestionFlowFileResult, List<TreasuryXlsErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
		String ipa = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
		String rowIuf = TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
		LocalDate billDate = row.getBillDate();

		try {
			TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(ingestionFlowFileResult.getOrganizationId(), rowIuf);

			if(existingTreasury != null) {
				boolean treasuryNotMatch = !existingTreasury.getBillCode().equals(TreasuryUtils.getBillCode(billDate, rowIuf)) || !existingTreasury.getBillYear().equals(String.valueOf(billDate.getYear()));
				if (treasuryNotMatch) {
					String errorMessage = String.format(
							"IUF %s already associated to another treasury for organization with IPA code %s",
							rowIuf, ipa);
					log.error(errorMessage);
					TreasuryXlsErrorDTO error = new TreasuryXlsErrorDTO(
							ingestionFlowFile.getFileName(),
							rowIuf,
							lineNumber, "IUF_ALREADY_ASSOCIATED", errorMessage);
					errorList.add(error);
					return false;
				}
			}

			Treasury treasury = treasuryService.insert(
					treasuryXlsMapper.map(row, ingestionFlowFile));

			ingestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
			return true;
		} catch (Exception e) {
			log.error("Error processing treasury csv with iuf {}: {}",
					rowIuf,
					e.getMessage());
			TreasuryXlsErrorDTO error = new TreasuryXlsErrorDTO(
					ingestionFlowFile.getFileName(),
					rowIuf,
					lineNumber, "PROCESS_EXCEPTION", e.getMessage());
			errorList.add(error);
			log.info("Current error list size after handleProcessingError: {}", errorList.size());
			return false;
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

		process(iterator, new ArrayList<>(), ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
		return ingestionFlowFileResult;
	}

}
