package it.gov.pagopa.payhub.activities.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilitiesTest {

    @Test
    void givenAddressAndNotPosteItalianeThenSuccess(){
        boolean result = Utilities.validateAddress("Via del test", true);
        assertTrue(result);
    }

    @Test
    void givenCivicAndNotPosteItalianeThenSuccess(){
        boolean result = Utilities.validateCivic("14", true);
        assertTrue(result);
    }
}
