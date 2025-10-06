package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TreasuryXlsServiceImpl extends XlsService<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult> {

	public TreasuryXlsServiceImpl() {
		super(filePath -> {
			try {
				return new XlsIterator<>(
						filePath,
						TreasuryXlsHeadersEnum.getHeaders(),
						TreasuryXlsRowMapper::new
				);
			} catch (Exception e) {
				throw new TreasuryXlsInvalidFileException("Cannot parse treasury Xls file \"%s\"".formatted(filePath.getFileName()), e);
			}
		});
	}

}
