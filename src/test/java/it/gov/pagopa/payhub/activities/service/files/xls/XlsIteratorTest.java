package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

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

	@BeforeAll
	public static void startup() throws IOException {
		sut = new XlsIterator<>(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_XLS_ALL_RECORDS_OK_0001.xls"),
				l -> mapperMock
		);
	}

	@AfterEach
	public void clearMock() {
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

}