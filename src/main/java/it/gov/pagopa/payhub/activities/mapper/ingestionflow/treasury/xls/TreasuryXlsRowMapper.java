package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreasuryXlsRowMapper {
	private final Map<String, Integer> headerToIndex;

	public TreasuryXlsRowMapper(List<String> headers) {
		this.headerToIndex = IntStream.range(0, headers.size())
						.boxed()
						.collect(Collectors.toMap(headers::get, i -> i));
	}

	public TreasuryXlsIngestionFlowFileDTO map(List<String> cells) {
		if(cells == null) {
			return null;
		}
		try {
			return TreasuryXlsIngestionFlowFileDTO.builder()
					.abiCode(mapOrElse(cells, this.headerToIndex.get("ABI"), String::trim, null)) //input=ABI -> headerToIndex -> output=index
					.cabCode(mapOrElse(cells, this.headerToIndex.get("CAB"), String::trim, null)) //input=CAB -> headerToIndex -> output=index
					.accountCode(mapOrElse(cells, this.headerToIndex.get("CONTO"), String::trim, null)) //input=CONTO -> headerToIndex -> output=index
					.currency(mapOrElse(cells, this.headerToIndex.get("DIVISA"), String::trim, null)) //input=DIVISA -> headerToIndex -> output=index
					.billDate(mapOrElse(cells, this.headerToIndex.get("DATA CONTABILE"), s -> DateUtil.getLocalDateTime(Double.parseDouble(s.trim())).toLocalDate(), null)) //input=DATA CONTABILE -> headerToIndex -> output=index
					.regionValueDate(mapOrElse(cells, this.headerToIndex.get("DATA VALUTA"), s -> DateUtil.getLocalDateTime(Double.parseDouble(s.trim())).toLocalDate(), null)) //input=DATA VALUTA -> headerToIndex -> output=index
					.billAmountCents(mapOrElse(cells, this.headerToIndex.get("IMPORTO"), s -> (long) (Double.parseDouble(s.trim())*100), null)) //input=IMPORTO -> headerToIndex -> output=index
					.sign(mapOrElse(cells, this.headerToIndex.get("SEGNO"), String::trim, null)) //input=SEGNO -> headerToIndex -> output=index
					.remittanceCode(mapOrElse(cells, this.headerToIndex.get("CAUSALE"), String::trim, null)) //input=CAUSALE -> headerToIndex -> output=index
					.checkNumber(mapOrElse(cells, this.headerToIndex.get("NUM. ASSEGNO"), String::trim, null)) //input=NUM. ASSEGNO -> headerToIndex -> output=index
					.bankReference(mapOrElse(cells, this.headerToIndex.get("RIF. BANCA"), String::trim, null)) //input=RIF. BANCA -> headerToIndex -> output=index
					.clientReference(mapOrElse(cells, this.headerToIndex.get("RIF. CLIENTE"), String::trim, null)) //input=RIF. CLIENTE -> headerToIndex -> output=index
					.remittanceDescription(mapOrElse(cells, this.headerToIndex.get("DESCRIZIONE"), String::trim, null)) //input=DESCRIZIONE -> headerToIndex -> output=index
					.extendedRemittanceDescription(mapOrElse(cells, this.headerToIndex.get("DESCRIZIONE ESTESA"), String::trim, null)) //input=DESCRIZIONE ESTESA -> headerToIndex -> output=index
					.build();
		} catch (Exception e) {
			throw new IllegalStateException("Mapping List<String to TreasuryXlsIngestionFlowFileDTO error: ", e);
		}
	}

	private <T> T mapOrElse(List<String> cells, Integer index, Function<String, T> mapper, T defaultValue) {
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
