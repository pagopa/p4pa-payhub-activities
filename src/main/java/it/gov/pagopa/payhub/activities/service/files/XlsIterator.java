package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.XlsRowMapper;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class XlsIterator<D> implements Closeable, Iterator<D> {

	private static final Logger log = LoggerFactory.getLogger(XlsIterator.class);
	private final POIFSFileSystem poifsFileSystem;
	private final DocumentInputStream documentInputStream;
	private final RecordFactoryInputStream recordFactoryInputStream;

	private XlsRowMapper<D> mapper;
	private final Function<List<String>,XlsRowMapper<D>> mapperBuildFunction;
	private int headerCount = -1;

	private SSTRecord excelStringList;

	private final Map<Integer, String> rowBuffer = new TreeMap<>();
	private int rowNum = -1;
	private int currentRow = -1;
	private boolean pendingRowReady = false;
	private List<String> pendingRow;

	public XlsIterator(Path file, Function<List<String>,XlsRowMapper<D>> mapperBuildFunction) throws IOException {
		this.poifsFileSystem = new POIFSFileSystem(file.toFile(), true);
		this.documentInputStream = poifsFileSystem.createDocumentInputStream("Workbook");
		this.recordFactoryInputStream = new RecordFactoryInputStream(documentInputStream, false);
		this.mapperBuildFunction = mapperBuildFunction;
	}

	@Override
	public void close() throws IOException {
		try {
			documentInputStream.close();
		} catch (Exception e) {
			log.warn("Error in closing documentInputStream", e);
		}
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
		if (nextRow.isPresent())
			return mapper.map(nextRow.get());
		else
			return mapper.map(handleLastLine()); //For last row readNextRow will return Optional.empty()
	}

	private void buildMapper() {
		List<String> headers = this.readNextRow()
				.orElseThrow(
						() -> new IllegalStateException("Headers not found, cannot create mapper for Treasury .xsl file")
				);
		headerCount = headers.size();
		mapper = this.mapperBuildFunction.apply(headers);
	}

	private Optional<List<String>> readNextRow() {
		Record eventRecord;
		while((eventRecord = recordFactoryInputStream.nextRecord()) != null) {
			processEventRecord(eventRecord);
			if(pendingRowReady) {
				return Optional.of(flushPending());
			}
		}
		//For last row while loop will terminate before a pending row is ready
		return Optional.empty();
	}

	private List<String> handleLastLine() {
		if(!rowBuffer.isEmpty()) {
			List<String> out = buildRow(rowBuffer);
			rowBuffer.clear();
			pendingRow = null;
			pendingRowReady = false;
			currentRow = -1;
			return out;
		} else {
			return Collections.emptyList();
		}
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
				pendingRow = buildRow(rowBuffer);
				pendingRowReady = true;
				rowBuffer.clear();
			}
			currentRow = row;
		}
		rowBuffer.put(column, value == null ? "" : value);
	}

	private List<String> flushPending() {
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
		if(headerCount==-1) {
			headerCount = rowBuffer.size();
		}
		return headerCount;
	}

}
