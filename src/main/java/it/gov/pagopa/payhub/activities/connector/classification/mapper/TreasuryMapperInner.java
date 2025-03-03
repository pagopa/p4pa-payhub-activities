package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.mapstruct.Mapper;

@Mapper
interface TreasuryMapperInner {
    TreasuryIuf map2Iuf(Treasury treasury);
    TreasuryIuv map2Iuv(Treasury treasury);
}
