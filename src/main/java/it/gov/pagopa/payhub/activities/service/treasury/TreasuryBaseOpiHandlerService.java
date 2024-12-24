package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;

import java.util.HashMap;
import java.util.Map;

public class TreasuryBaseOpiHandlerService {
    private static final Map<Class<?>, TreasuryValidatorService<?>> validators = new HashMap<>();
    private static final Map<Class<?>, TreasuryMapperService<?, ?>> mappers = new HashMap<>();

    static {
        validators.put(FlussoGiornaleDiCassa.class, new TreasuryOpi14ValidatorService());
        validators.put(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class, new TreasuryOpi161ValidatorService());
        mappers.put(FlussoGiornaleDiCassa.class, new TreasuryOpi14MapperService());
        mappers.put(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class, new TreasuryOpi161MapperService());
    }

    @SuppressWarnings("unchecked")
    public <T> TreasuryValidatorService<T> getValidator(Class<T> clazz) {
        return (TreasuryValidatorService<T>) validators.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T, U> TreasuryMapperService<T, U> getMapper(Class<T> clazz) {
        return (TreasuryMapperService<T, U>) mappers.get(clazz);
    }

}
