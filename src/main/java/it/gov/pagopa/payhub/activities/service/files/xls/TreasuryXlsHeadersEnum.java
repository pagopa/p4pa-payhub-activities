package it.gov.pagopa.payhub.activities.service.files.xls;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TreasuryXlsHeadersEnum {
	ABI("ABI"),
	CAB("CAB"),
	CONTO("CONTO"),
	DIVISA("DIVISA"),
	DATA_CONTABILE("DATA CONTABILE"),
	DATA_VALUTA("DATA VALUTA"),
	IMPORTO("IMPORTO"),
	SEGNO("SEGNO"),
	CAUSALE("CAUSALE"),
	NUM_ASSEGNO("NUM. ASSEGNO"),
	RIF_BANCA("RIF. BANCA"),
	RIF_CLIENTE("RIF. CLIENTE"),
	DESCRIZIONE("DESCRIZIONE"),
	DESCRIZIONE_ESTESA("DESCRIZIONE ESTESA");

	private final String value;

	TreasuryXlsHeadersEnum(String value) {
		this.value = value;
	}

	public static List<String> getHeaders() {
		return Arrays.stream(TreasuryXlsHeadersEnum.values())
				.map(TreasuryXlsHeadersEnum::getValue)
				.collect(Collectors.toList());
	}
}
