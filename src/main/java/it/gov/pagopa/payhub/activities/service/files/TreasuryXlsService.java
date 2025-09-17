package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

@Service
@Slf4j
public class TreasuryXlsService {

	public <T> T readXls(Path xlsFilePath, Function<Iterator<TreasuryXlsIngestionFlowFileDTO>, T> rowProcessor) throws IOException {
		try(TreasuryXlsIterator xlsIterator = new TreasuryXlsIterator(xlsFilePath)) {
			return rowProcessor.apply(xlsIterator);
		} catch (Exception e) {
			throw new IOException("Error while reading xsl file \"%s\": %s".formatted(xlsFilePath, e.getMessage()), e);
		}
	}

}
