package it.gov.pagopa.payhub.activities.service.files.xls;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

public abstract class XlsService<D, O> {

	private final Function<Path, XlsIterator<D>> xlsIteratorBuilder;

	protected XlsService(Function<Path, XlsIterator<D>> xlsIteratorBuilder) {
		this.xlsIteratorBuilder = xlsIteratorBuilder;
	}

	public O readXls(Path xlsFilePath, Function<Iterator<D>, O> rowProcessor) throws IOException {
		try(XlsIterator<D> xlsIterator = xlsIteratorBuilder.apply(xlsFilePath)) {
			return rowProcessor.apply(xlsIterator);
		} catch (Exception e) {
			throw new IOException("Error while reading xsl file \"%s\": %s".formatted(xlsFilePath, e.getMessage()), e);
		}
	}

}
