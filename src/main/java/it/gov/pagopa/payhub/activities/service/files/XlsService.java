package it.gov.pagopa.payhub.activities.service.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

@Service
@Slf4j
public class XlsService<DTO, O> {

	private final Function<Path, XlsIterator<DTO>> xlsIteratorBuilder;

	public XlsService(Function<Path, XlsIterator<DTO>> xlsIteratorBuilder) {
		this.xlsIteratorBuilder = xlsIteratorBuilder;
	}

	public O readXls(Path xlsFilePath, Function<Iterator<DTO>, O> rowProcessor) throws IOException {
		try(XlsIterator<DTO> xlsIterator = xlsIteratorBuilder.apply(xlsFilePath)) {
			return rowProcessor.apply(xlsIterator);
		} catch (Exception e) {
			throw new IOException("Error while reading xsl file \"%s\": %s".formatted(xlsFilePath, e.getMessage()), e);
		}
	}

}
