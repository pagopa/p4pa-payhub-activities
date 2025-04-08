package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebtPositionUtilitiesTest {

	@Test
	void iuv2navShouldPrependAuxDigit() {
		String iuv = "12345";
		String result = DebtPositionUtilities.iuv2nav(iuv);
		assertEquals("312345", result);
	}
}