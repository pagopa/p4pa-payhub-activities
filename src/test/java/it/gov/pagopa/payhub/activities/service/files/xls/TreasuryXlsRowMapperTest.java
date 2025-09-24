package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCAL_DATE_2024;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsRowMapperTest {

	private final List<String> headers = Arrays.stream(TreasuryXlsHeadersEnum.values())
			.map(TreasuryXlsHeadersEnum::getValue)
			.toList();

	private final TreasuryXlsRowMapper sut = new TreasuryXlsRowMapper(headers);

	@Test
	void givenFullTreasuryXlsCellsWhenMappingThenOK() {
		//GIVEN
		List<String> values = headers.stream().map(s -> s + "_value").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "45427.0");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_VALUTA.getValue()), "45427.0");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		//WHEN
		TreasuryXlsIngestionFlowFileDTO result = sut.map(values, 0);
		//THEN
		Assertions.assertEquals(TreasuryXlsHeadersEnum.ABI.getValue() + "_value", result.getAbiCode());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.CAB.getValue() + "_value", result.getCabCode());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.CONTO.getValue() + "_value", result.getAccountCode());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.DIVISA.getValue() + "_value", result.getCurrency());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.SEGNO.getValue() + "_value", result.getSign());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.CAUSALE.getValue() + "_value", result.getRemittanceCode());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.NUM_ASSEGNO.getValue() + "_value", result.getCheckNumber());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.RIF_BANCA.getValue() + "_value", result.getBankReference());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.RIF_CLIENTE.getValue() + "_value", result.getClientReference());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.DESCRIZIONE.getValue() + "_value", result.getRemittanceDescription());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value", result.getExtendedRemittanceDescription());
		Assertions.assertEquals(LOCAL_DATE_2024, result.getBillDate());
		Assertions.assertEquals(LOCAL_DATE_2024, result.getRegionValueDate());
		Assertions.assertEquals(1235L, result.getBillAmountCents());

		TestUtils.checkNotNullFields(result, "abiCode", "cabCode", "accountCode", "currency", "sign",
				"remittanceCode", "checkNumber", "bankReference", "clientReference", "remittanceDescription",
				"regionValueDate"
		);
	}

	@Test
	void givenNullNotRequiredFieldsWhenMappingThenOK() {
		//GIVEN
		List<String> values = Arrays.asList(new String[headers.size()]);
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "45427.0");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN
		TreasuryXlsIngestionFlowFileDTO result = sut.map(values, 0);
		//THEN
		Assertions.assertNull(result.getAbiCode());
		Assertions.assertNull(result.getCabCode());
		Assertions.assertNull(result.getAccountCode());
		Assertions.assertNull(result.getCurrency());
		Assertions.assertNull(result.getSign());
		Assertions.assertNull(result.getRemittanceCode());
		Assertions.assertNull(result.getCheckNumber());
		Assertions.assertNull(result.getBankReference());
		Assertions.assertNull(result.getClientReference());
		Assertions.assertNull(result.getRemittanceDescription());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value", result.getExtendedRemittanceDescription());
		Assertions.assertEquals(LOCAL_DATE_2024, result.getBillDate());
		Assertions.assertNull(result.getRegionValueDate());
		Assertions.assertEquals(1235L, result.getBillAmountCents());

		TestUtils.checkNotNullFields(result, "abiCode", "cabCode", "accountCode", "currency", "sign",
				"remittanceCode", "checkNumber", "bankReference", "clientReference", "remittanceDescription",
				"regionValueDate"
		);
	}

	@Test
	void givenEmptyNotRequiredFieldsWhenMappingThenOK() {
		//GIVEN
		List<String> values = headers.stream().map(s -> "").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "45427.0");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN
		TreasuryXlsIngestionFlowFileDTO result = sut.map(values, 0);
		//THEN
		Assertions.assertNull(result.getAbiCode());
		Assertions.assertNull(result.getCabCode());
		Assertions.assertNull(result.getAccountCode());
		Assertions.assertNull(result.getCurrency());
		Assertions.assertNull(result.getSign());
		Assertions.assertNull(result.getRemittanceCode());
		Assertions.assertNull(result.getCheckNumber());
		Assertions.assertNull(result.getBankReference());
		Assertions.assertNull(result.getClientReference());
		Assertions.assertNull(result.getRemittanceDescription());
		Assertions.assertEquals(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value", result.getExtendedRemittanceDescription());
		Assertions.assertEquals(LOCAL_DATE_2024, result.getBillDate());
		Assertions.assertNull(result.getRegionValueDate());
		Assertions.assertEquals(1235L, result.getBillAmountCents());

		TestUtils.checkNotNullFields(result, "abiCode", "cabCode", "accountCode", "currency", "sign",
				"remittanceCode", "checkNumber", "bankReference", "clientReference", "remittanceDescription",
				"regionValueDate"
		);
	}

	@Test
	void givenEmptyRequiredTreasuryXlsCellsWhenMappingThenKO() {
		//GIVEN
		List<String> values = headers.stream().map(s -> "").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), null);
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN, THEN
		IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> sut.map(values, 0));
		Assertions.assertEquals("Xls Cell with name \"%s\" must not be null or blank".formatted(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), ex.getMessage());
	}

	@Test
	void givenNullRequiredTreasuryXlsCellsWhenMappingThenKO() {
		//GIVEN
		List<String> values = headers.stream().map(s -> "").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN, THEN
		IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> sut.map(values, 0));
		Assertions.assertEquals("Xls Cell with name \"%s\" must not be null or blank".formatted(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), ex.getMessage());
	}

	@Test
	void givenImproperLocalDateInTreasuryXlsCellsWhenMappingThenKO() {
		//GIVEN
		List<String> values = headers.stream().map(s -> "").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "LocalDate");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "12.35");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN, THEN
		IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> sut.map(values, 0));
		Assertions.assertEquals("Error in parsing LocalDate from value \"LocalDate\" for Xls cell \"%s\"".formatted(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), ex.getMessage());
	}

	@Test
	void givenImproperLongInTreasuryXlsCellsWhenMappingThenKO() {
		//GIVEN
		List<String> values = headers.stream().map(s -> "").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "45427.0");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "Long");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue() + "_value");
		//WHEN, THEN
		IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> sut.map(values, 0));
		Assertions.assertEquals("Error in parsing Long from value \"Long\" for Xls cell \"%s\"".formatted(TreasuryXlsHeadersEnum.IMPORTO.getValue()), ex.getMessage());
	}

	@Test
	void givenEmptyListTreasuryXlsWhenMappingThenNull() {
		//WHEN
		TreasuryXlsIngestionFlowFileDTO actualResult = sut.map(Collections.emptyList(), 0);
		//THEN
		Assertions.assertNull(actualResult);
	}

	@Test
	void givenNullListTreasuryXlsWhenMappingThenNull() {
		//WHEN
		TreasuryXlsIngestionFlowFileDTO actualResult = sut.map(null, 0);
		//THEN
		Assertions.assertNull(actualResult);
	}

}