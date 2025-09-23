package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XlsIteratorTest {

	private static final TreasuryXlsRowMapper mapperMock = Mockito.mock(TreasuryXlsRowMapper.class);

	@SuppressWarnings("unchecked")
	private final ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);

	private static XlsIterator<TreasuryXlsIngestionFlowFileDTO> sut;

	private final List<String> row1 = List.of("02008","02017","000101059865","EUR","45494.0","45494.0","12.34","+","48BO","","0920299486000106","","Data Ordine: 21/07/2024; Descr","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072600 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM");
	private final List<String> row2 = List.of("02009","02018","000101059865","EUR","45495.0","45495.0","12.34","+","48BO","","0920299486000106","","Data Ordine: 22/07/2024; Descr","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM");
	private final List<String> row3 = List.of("02010","02019","000101059865","EUR","45496.0","45496.0","12.34","+","48BO","","0920299486000106","","Data Ordine: 23/07/2024; Descr","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072602 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM");
	private final List<String> row4 = List.of("02011","02020","000101059865","EUR","45496.0","45496.0","13.34","+","48BO","","0920299486000106","","Data Ordine: 23/07/2024; Descr","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072603 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM");
	private final List<String> row5 = List.of("02012","02021","000101059865","EUR","45496.0","45496.0","14.34","-","48BO","","0920299486000106","","Data Ordine: 23/07/2024; Descr","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072604 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM");
	private final List<String> row1_test14 = List.of("02008","02017","000101059865","EUR","45494.0","45494.0","12.34","+","48BO","","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072600 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM","Data Ordine: 21/07/2024; Descr","0920299486000106","");
	private final List<String> row3_test14 = List.of("02010","02019","000101059865","EUR","45496.0","45496.0","12.34","+","48BO","","Data Ordine: 21/07/2024; Descrizione Ordinante: FIDEURAM INTESA SANPAOLO PRIVATE BANKING SPA                          PIAZZA SAN :BI2:FIBKITMMXXX :BE1:IPA TEST 2 :IB1:IT13R0200802017000101059865 :IB2:IT52Q0329620095000063193091 :TID:1001241280000102 :DTE:240507 :DTN:IPA TEST 2 :ERI:EUR 000000000019312 :IM2:000000000019312 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072602 :SEC:CASH :OR1:FIDEURAM INTESA SANPAO LO PRIVATE BA NKING SPA PIAZZA SAN CARLO 156 10121 TORINO T :TR1:INTESASANPAOLO CBILL PUBBLICA AMM","","","");

	@BeforeAll
	static void startup() throws IOException {
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_1_XLS_ALL_RECORDS_OK.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);
	}

	@AfterEach
	void clearMock() {
		Mockito.clearInvocations(mapperMock);
	}

	@Test
	@Order(1)
	void givenFileIsNotFinishedWhenHasNextThenReturnTrue() {
		//WHEN
		boolean hasNext = sut.hasNext();
		//THEN
		Assertions.assertTrue(hasNext);
	}

	@Test
	@Order(2)
	void givenFileIsNotFinishedWhenNextThenReturnRow1() {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		//WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row1, list);
	}

	@Test
	@Order(3)
	void givenFileIsNotFinishedWhenNextThenReturnRow2() {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		//WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row2, list);
	}

	@Test
	@Order(4)
	void givenFileIsNotFinishedWhenNextThenReturnRow3() {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		//WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row3, list);
	}

	@Test
	@Order(5)
	void givenFileIsNotFinishedWhenNextThenReturnRow4() {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		//WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row4, list);
	}

	@Test
	@Order(6)
	void givenFileIsNotFinishedWhenNextThenReturnRow5() {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		//WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row5, list);
	}

	@Test
	@Order(7)
	void givenFileIsFinishedWhenNextThenThrowNoSuchElementException() {
		//WHEN, THEN
		assertThrows(NoSuchElementException.class, () -> sut.next());
	}

	@Test
	@Order(8)
	void givenFileIsFinishedWhenHasNextThenReturnFalse() {
		//WHEN
		boolean hasNext = sut.hasNext();
		//THEN
		Assertions.assertFalse(hasNext);
	}

	@Test
	@Order(9)
	void givenStreamIsClosedWhenHasNextOrNextThenThrowIllegalStateException() throws IOException {
		//GIVEN
		sut.close();
		//WHEN, THEN
		IllegalStateException ex1 = assertThrows(IllegalStateException.class, () -> sut.hasNext());
		Assertions.assertEquals("cannot perform requested operation on a closed stream", ex1.getMessage());
		//WHEN, THEN
		IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> sut.next());
		Assertions.assertEquals("cannot perform requested operation on a closed stream", ex2.getMessage());
	}

	@Test
	@Order(10)
	void givenStreamIsAlreadyClosedWhenCloseThenNothingIsThrown() {
		//WHEN, THEN
		Assertions.assertDoesNotThrow(() -> sut.close());
	}

	@Test
	@Order(11)
	void givenFileWithNoHeadersWhenNextThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_4_XLS_WITH_NO_HEADERS.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);
		// WHEN, THEN
		TreasuryXlsInvalidFileException ex = assertThrows(TreasuryXlsInvalidFileException.class, () -> sut.next());
		String expectedMissingHeaders = String.join(", ", TreasuryXlsHeadersEnum.getHeaders());
		Assertions.assertEquals("Missing headers in file \"IPA_TEST_4_XLS_WITH_NO_HEADERS.xls\", cannot create mapper: %s".formatted(expectedMissingHeaders), ex.getMessage());
		sut.close();
	}

	@Test
	@Order(12)
	void givenEmptyFileWhenNextThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_5_XLS_EMPTY.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);
		// WHEN, THEN
		TreasuryXlsInvalidFileException ex = assertThrows(TreasuryXlsInvalidFileException.class, () -> sut.next());

		Assertions.assertEquals("Headers not found in empty file \"IPA_TEST_5_XLS_EMPTY.xls\", cannot create mapper", ex.getMessage());
		sut.close();
	}

	@Test
	@Order(13)
	void givenFileWithNoDataWhenNextThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_6_XLS_WITH_ONLY_HEADERS.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);
		Mockito.when(mapperMock.map(new ArrayList<>())).thenReturn(null);
		// WHEN, THEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		Assertions.assertNull(next);
		sut.close();
	}

	@Test
	@Order(14)
	void givenFileWithIncompleteRowWhenNextThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		Mockito.when(mapperMock.map(Mockito.anyList()))
				.thenReturn(new TreasuryXlsIngestionFlowFileDTO());
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_7_XLS_WITH_INCOMPLETE_ROW.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);

		// WHEN
		TreasuryXlsIngestionFlowFileDTO next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		List<String> list = argumentCaptor.getValue();
		Assertions.assertEquals(row1_test14, list);

		Mockito.clearInvocations(mapperMock);

		// WHEN
		next = sut.next();
		//THEN
		Assertions.assertNotNull(next);
		Mockito.verify(mapperMock).map(argumentCaptor.capture());
		list = argumentCaptor.getValue();
		Assertions.assertEquals(row3_test14, list);

		sut.close();
	}

	@Test
	@Order(15)
	void givenFileWithMissingColumnWhenNextThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_8_XLS_WITH_MISSING_COLUMN.xls"),
				TreasuryXlsHeadersEnum.getHeaders(),
				l -> mapperMock
		);
		Mockito.when(mapperMock.map(new ArrayList<>())).thenReturn(null);
		// WHEN, THEN
		TreasuryXlsInvalidFileException ex = assertThrows(TreasuryXlsInvalidFileException.class, () -> sut.next());
		String expectedMissingHeaders = String.join(", ", List.of(
				TreasuryXlsHeadersEnum.ABI.getValue(),
				TreasuryXlsHeadersEnum.CAB.getValue()
		));
		Assertions.assertEquals("Missing headers in file \"IPA_TEST_8_XLS_WITH_MISSING_COLUMN.xls\", cannot create mapper: %s".formatted(expectedMissingHeaders), ex.getMessage());
		sut.close();
	}

}