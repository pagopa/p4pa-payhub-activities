package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class XlsRowMapper<D> {

	public abstract D map(List<String> cells);

	protected final Map<String, Integer> headerToIndex;

	protected XlsRowMapper(List<String> headers) {
		this.headerToIndex = IntStream.range(0, headers.size())
				.boxed()
				.collect(Collectors.toMap(headers::get, i -> i));
	}

	int getHeaderIndex(String header) {
		return this.headerToIndex.get(header);
	}

	<T> T mapOrElse(List<String> cells, Integer index, Function<String, T> mapper, T defaultValue) {
		String raw = null;
		if (index!=null && index < cells.size()) {
			raw = cells.get(index);
		}
		if(raw != null) {
			return mapper.apply(raw);
		} else {
			return defaultValue;
		}
	}

}
