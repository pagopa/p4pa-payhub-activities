package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XlsIterator<D> implements Closeable, Iterator<D> {

	private final Path filePath;
	private final POIFSFileSystem poifsFileSystem;
	private final DocumentInputStream documentInputStream;
	private final RecordFactoryInputStream recordFactoryInputStream;

	private XlsRowMapper<D> mapper;
	private final Function<List<String>,XlsRowMapper<D>> mapperBuildFunction;

	private final List<String> requiredHeaderList;
	private int headersCount = -1;

	private SSTRecord excelStringList;

	private final Map<Integer, String> rowBuffer = new TreeMap<>();
	private int rowNum = -1;
	private int currentRow = -1;
	private boolean pendingRowReady = false;
	private List<String> pendingRow;

	public XlsIterator(Path filePath, List<String> requiredHeaderList, Function<List<String>, XlsRowMapper<D>> mapperBuildFunction) throws IOException {
		this.filePath = filePath;
		this.requiredHeaderList = requiredHeaderList;
		this.poifsFileSystem = new POIFSFileSystem(filePath.toFile(), true);
		this.documentInputStream = poifsFileSystem.createDocumentInputStream("Workbook");
		this.recordFactoryInputStream = new RecordFactoryInputStream(documentInputStream, false);
		this.mapperBuildFunction = mapperBuildFunction;
	}

	@Override
	public void close() throws IOException {
		documentInputStream.close();
		poifsFileSystem.close();
	}

	@Override
	public boolean hasNext() {
		return documentInputStream.available() > 0;
	}

	@Override
	public D next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if(mapper==null) {
			buildMapper();
		}

		Optional<List<String>> nextRow = readNextRow();
		return nextRow
				.map(strings -> mapper.map(strings))
				.orElse(null);
	}

	private void buildMapper() {
		List<String> headers = this.readNextRow()
				.orElseThrow(
						() -> new TreasuryXlsInvalidFileException("Headers not found in empty file \"%s\", cannot create mapper".formatted(filePath.getFileName()), null)
				);
		if(!headers.containsAll(requiredHeaderList)) {
			String missingHeaders = requiredHeaderList.stream()
					.filter(h -> !headers.contains(h))
					.collect(Collectors.joining(", "));
			throw new TreasuryXlsInvalidFileException("Missing headers in file \"%s\", cannot create mapper: %s".formatted(filePath.getFileName(), missingHeaders), null);
		}
		mapper = this.mapperBuildFunction.apply(headers);
	}

	private Optional<List<String>> readNextRow() {
		Record eventRecord;
		while((eventRecord = recordFactoryInputStream.nextRecord()) != null) {
			processEventRecord(eventRecord);
			if(pendingRowReady) {
				return Optional.of(flushPendingRow());
			}
		}
		//For last row while loop will terminate before a pending row is ready
		if(!rowBuffer.isEmpty()) {
			preparePendingRow();
			return Optional.of(flushPendingRow());
		}
		return Optional.empty();
	}

	private void processEventRecord(Record eventRecord) {
		switch (eventRecord.getSid()) {
			case RowRecord.sid:
				rowNum++;
				break;
			case NumberRecord.sid: //number record
				NumberRecord numrec = (NumberRecord) eventRecord;
				addCell(
						numrec.getRow(),
						numrec.getColumn(),
						Double.toString(numrec.getValue())
				);
				break;
			// SSTRecords store an array of unique strings used in Excel.
			case SSTRecord.sid: //full list of string in .xsl file as an "array"
				excelStringList = (SSTRecord) eventRecord;
				break;
			case LabelSSTRecord.sid: //index of string related to previuous received (SSTRecord) record, with "full list of string in .xsl file as an array"
				LabelSSTRecord lrec = (LabelSSTRecord) eventRecord;
				addCell(
						lrec.getRow(),
						lrec.getColumn(),
						excelStringList.getString(lrec.getSSTIndex()).getString()
				);
				break;
			default:
				break; // not interested in other events
		}
	}

	private void addCell(int row, int column, String value) {
		if (row != currentRow) {
			if( currentRow != -1 && !rowBuffer.isEmpty()) {
				preparePendingRow();
			}
			currentRow = row;
		}
		rowBuffer.put(column, value == null ? "" : value);
	}

	private void preparePendingRow() {
		pendingRow = buildRow(rowBuffer);
		pendingRowReady = true;
		rowBuffer.clear();
	}

	private List<String> flushPendingRow() {
		List<String> out = pendingRow;
		pendingRowReady = false;
		pendingRow = null;
		return out;
	}

	private List<String> buildRow(Map<Integer, String> rowBuffer) {
	 	List<String> cells = Arrays.asList(new String[computeHeaderCount()]);
		rowBuffer.forEach(cells::set);
		return cells;
	}

	private int computeHeaderCount() {
		if(headersCount==-1) {
			headersCount = rowBuffer.size();
		}
		return headersCount;
	}

}
