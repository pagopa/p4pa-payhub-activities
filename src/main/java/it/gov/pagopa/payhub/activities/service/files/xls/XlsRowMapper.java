package it.gov.pagopa.payhub.activities.service.files.xls;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class XlsRowMapper<D> {

	/**
	 * @param cells list of string values of row to map
	 * @param rowIndex 0-based index of row to map
	 * @return entity built by mapping the string value contained in parameter cells
	 */
	public abstract D map(List<String> cells, int rowIndex);

	protected final Map<String, Integer> headerToIndex;

	protected XlsRowMapper(List<String> headers) {
		this.headerToIndex = IntStream.range(0, headers.size())
				.boxed()
				.collect(Collectors.toMap(headers::get, i -> i));
	}

	protected int getHeaderIndex(String header) {
		return this.headerToIndex.get(header);
	}

	protected String mapString(List<String> cells, Integer index) {
		return mapOrElse(cells, index, String::trim, null);
	}

	protected <T> T mapOrElse(List<String> cells, Integer index, Function<String, T> mapper, T defaultValue) {
		return mapOrElse(cells, index, (ignored, cellValue) -> mapper.apply(cellValue), null, defaultValue);
	}

	protected <T> T mapOrElse(List<String> cells, String cellName, Integer index, BiFunction<String, String, T> mapper, T defaultValue) {
		return mapOrElse(cells, index, mapper, cellName, defaultValue);
	}

	private <T> T mapOrElse(List<String> cells, Integer index, BiFunction<String, String, T> biFunction, String cellName, T defaultValue) {
		String cellValue = cells.get(index);
		if(cellValue != null && !cellValue.isBlank()) {
			return biFunction.apply(cellName, cellValue);
		} else {
			return defaultValue;
		}
	}

	protected <T> T map(List<String> cells, String cellName, Integer index, Function<String, T> mapper) {
		return map(cells, index, (ignored, cellValue) -> mapper.apply(cellValue), cellName);
	}

	protected <T> T map(List<String> cells, String cellName, Integer index, BiFunction<String, String, T> mapper) {
		return map(cells, index, mapper, cellName);
	}

	private <T> T map(List<String> cells, Integer index, BiFunction<String, String, T> biFunction, String cellName) {
		String cellValue =  cells.get(index);
		if(cellValue != null && !cellValue.isBlank()) {
			return biFunction.apply(cellName, cellValue);
		} else {
			throw new IllegalStateException("La cella con nome \"%s\" non puo' essere vuota".formatted(cellName));
		}
	}

}
