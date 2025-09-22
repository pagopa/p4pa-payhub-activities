package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsRowMapperTest {

	private final List<String> headers = Arrays.stream(TreasuryXlsHeadersEnum.values())
			.map(TreasuryXlsHeadersEnum::getValue)
			.toList();

	private final TreasuryXlsRowMapper sut = new TreasuryXlsRowMapper(headers);

	@Test
	void map() {
		//GIVEN
		List<String> values = headers.stream().map(s -> s + "_value").collect(Collectors.toCollection(ArrayList::new));
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), "123");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.DATA_VALUTA.getValue()), "123");
		values.set(headers.indexOf(TreasuryXlsHeadersEnum.IMPORTO.getValue()), "123");
		//WHEN
		TreasuryXlsIngestionFlowFileDTO result = sut.map(values);
		//THEN
		Assertions.assertEquals(TreasuryXlsHeadersEnum.ABI.getValue() + "_value", result.getAbiCode());
	}
}