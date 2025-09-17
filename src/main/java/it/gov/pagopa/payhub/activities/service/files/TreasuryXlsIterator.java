package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TreasuryXlsIterator implements Closeable, Iterator<TreasuryXlsIngestionFlowFileDTO> {

	private final POIFSFileSystem poifsFileSystem;
	private final DocumentInputStream documentInputStream;
	private final RecordFactoryInputStream recordFactoryInputStream;

	private TreasuryXlsRowMapper mapper;
	private int headerCount = -1;

	private SSTRecord excelStringList;

	private final Map<Integer, String> rowBuffer = new TreeMap<>();
	private int currentRow = -1;
	private boolean pendingRowReady = false;
	private List<String> pendingRow;

	public TreasuryXlsIterator(Path file) throws IOException {
		this.poifsFileSystem = new POIFSFileSystem(file.toFile(), true);
		this.documentInputStream = poifsFileSystem.createDocumentInputStream("Workbook");
		this.recordFactoryInputStream = new RecordFactoryInputStream(documentInputStream, false);
	}

	@Override
	public void close() throws IOException {
		try {
			documentInputStream.close();
		} catch (Exception ignore) {}
		poifsFileSystem.close();
	}

	@Override
	public boolean hasNext() {
		return documentInputStream.available() > 0;
	}

	@Override
	public TreasuryXlsIngestionFlowFileDTO next() {
		if(mapper==null) {
			prepareMapper();
		}

		Optional<List<String>> nextRow = readNextRow();
		if (nextRow.isPresent())
			return mapper.map(nextRow.get());
		else
			return mapper.map(handleLastLine()); //For last row readNextRow will return Optional.empty()
	}

	private void prepareMapper() {
		List<String> headers = this.readNextRow()
				.orElseThrow(
						() -> new IllegalStateException("Headers not found, cannot create mapper for Treasury .xsl file")
				);
		headerCount = headers.size();
		mapper = new TreasuryXlsRowMapper(headers);
	}

	private Optional<List<String>> readNextRow() {
		Record record;
		while((record = recordFactoryInputStream.nextRecord()) != null) {
			processRecord(record);
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
			return null;
		}
	}

	private void processRecord(Record record) {
		switch (record.getSid()) {
			// the BOFRecord can represent either the beginning of a sheet or the workbook
			case RowRecord.sid: //TODO understand if we are interested in the row count, arrives before headers and row
				RowRecord rowrec = (RowRecord) record;
				break;
			case NumberRecord.sid: //number record
				NumberRecord numrec = (NumberRecord) record;
				addCell(
						numrec.getRow(),
						numrec.getColumn(),
						Double.toString(numrec.getValue())
				);
				break;
			// SSTRecords store an array of unique strings used in Excel.
			case SSTRecord.sid: //full list of string in .xsl file as an "array"
				excelStringList = (SSTRecord) record;
				break;
			case LabelSSTRecord.sid: //index of string related to previuous received (SSTRecord) record, with "full list of string in .xsl file as an array"
				LabelSSTRecord lrec = (LabelSSTRecord) record;
				addCell(
						lrec.getRow(),
						lrec.getColumn(),
						excelStringList.getString(lrec.getSSTIndex()).getString()
				);
				break;
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
