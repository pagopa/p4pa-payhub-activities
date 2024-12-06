package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDto;
import it.gov.pagopa.payhub.activities.xsd.treasury.FlussoGiornaleDiCassa;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Lazy
@Service
public class TreasuryMapperService implements Function<FlussoGiornaleDiCassa, TreasuryDto> {

    @Override
    public TreasuryDto apply(FlussoGiornaleDiCassa o) {
        return TreasuryDto.builder()



                .build();
    }



}
