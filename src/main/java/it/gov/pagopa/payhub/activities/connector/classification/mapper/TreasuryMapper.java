package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TreasuryMapper {
    private final TreasuryMapperInner mapperInner;

    public TreasuryMapper(TreasuryMapperInner mapperInner) {
        this.mapperInner = mapperInner;
    }

    public TreasuryIuf map2Iuf(Treasury treasury) {
        if (treasury != null) {
            if (!StringUtils.isEmpty(treasury.getIuf())) {
                return mapperInner.map2Iuf(treasury);
            } else {
                throw new IllegalArgumentException("Provided Treasury without IUF: " + treasury.getTreasuryId());
            }
        } else {
            return null;
        }
    }

    public TreasuryIuv map2Iuv(Treasury treasury) {
        if (treasury != null) {
            if (!StringUtils.isEmpty(treasury.getIuv())) {
                return mapperInner.map2Iuv(treasury);
            } else {
                throw new IllegalArgumentException("Provided Treasury without IUV: " + treasury.getTreasuryId());
            }
        } else {
            return null;
        }
    }
}
