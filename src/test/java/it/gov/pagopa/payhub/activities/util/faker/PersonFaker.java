package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.EntityTypeEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;

public class PersonFaker {

    public static PersonDTO buildPersonDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(PersonDTO.class)
                .entityType(EntityTypeEnum.F)
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
