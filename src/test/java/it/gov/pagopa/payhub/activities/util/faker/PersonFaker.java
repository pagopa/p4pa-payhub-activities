package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;

public class PersonFaker {

    public static PersonDTO buildPersonDTO(){
        return PersonDTO.builder()
                .entityType("uniqueIdentifierType")
                .fiscalCode("uniqueIdentifierCode")
                .fullName("fullName")
                .address("address")
                .civic("civic")
                .postalCode("postalCode")
                .location("location")
                .province("province")
                .nation("nation")
                .email("email@test.it")
                .build();
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO buildPaymentsPersonDTO(){
        return it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO.builder()
                .entityType("uniqueIdentifierType")
                .fiscalCode("uniqueIdentifierCode")
                .fullName("fullName")
                .address("address")
                .civic("civic")
                .postalCode("postalCode")
                .location("location")
                .province("province")
                .nation("nation")
                .email("email@test.it")
                .build();
    }
}
