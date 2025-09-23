package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Lazy
@Service
public class TreasuryXlsServiceImpl extends XlsService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult> {

	public TreasuryXlsServiceImpl() {
		super(filePath -> {
			try {
				return new XlsIterator<>(filePath, TreasuryXlsRowMapper::new);
			} catch (IOException e) {
				throw new TreasuryXlsInvalidFileException("Cannot parse treasury Xls file \"%s\"".formatted(filePath.getFileName()), e);
			}
		});
	}
}
