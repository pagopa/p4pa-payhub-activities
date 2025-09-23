package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

public abstract class XlsService<D, O> {

	private final Function<Path, XlsIterator<D>> xlsIteratorBuilder;

	protected XlsService(Function<Path, XlsIterator<D>> xlsIteratorBuilder) {
		this.xlsIteratorBuilder = xlsIteratorBuilder;
	}

	public O readXls(Path xlsFilePath, Function<Iterator<D>, O> rowProcessor) {
		try(XlsIterator<D> xlsIterator = xlsIteratorBuilder.apply(xlsFilePath)) {
			return rowProcessor.apply(xlsIterator);
		} catch (Exception e) {
			throw new TreasuryXlsInvalidFileException("Cannot parse treasury Xls file \"%s\"".formatted(xlsFilePath.getFileName()), e);
		}
	}

}
