package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;

public class PersonFaker {

    public static PersonDTO buildPersonDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(PersonDTO.class)
                .entityType(PersonDTO.EntityTypeEnum.F)
                .fiscalCode("uniqueIdentifierCode")
                .fullName("fullName")
                .address("address")
                .civic("civic")
                .postalCode("postalCode")
                .location("location")
                .province("province")
                .nation("nation")
                .email("email@test.it");
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO buildPaymentsPersonDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO.class)
                .entityType(it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO.EntityTypeEnum.F)
                .fiscalCode("uniqueIdentifierCode")
                .fullName("fullName")
                .address("address")
                .civic("civic")
                .postalCode("postalCode")
                .location("location")
                .province("province")
                .nation("nation")
                .email("email@test.it");
    }
}
