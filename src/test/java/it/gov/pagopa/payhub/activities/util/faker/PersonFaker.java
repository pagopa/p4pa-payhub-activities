package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.PersonDTO;

public class PersonFaker {

    public static PersonDTO buildPersonDTO(){
        return PersonDTO.builder()
                .uniqueIdentifierType("uniqueIdentifierType")
                .uniqueIdentifierCode("uniqueIdentifierCode")
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
