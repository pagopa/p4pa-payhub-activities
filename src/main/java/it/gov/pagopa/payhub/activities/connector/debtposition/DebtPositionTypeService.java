package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;

/**
 * This interface provides methods that manage debt positions type within the related microservice.
 */
public interface DebtPositionTypeService {
    /**
     * Creates a new debt position type using the provided request body.
     *
     * @param debtPositionTypeRequestBody the request body containing the details of the debt position type to create
     * @return the created DebtPositionType
     */
    DebtPositionType createDebtPositionType(DebtPositionTypeRequestBody debtPositionTypeRequestBody);

}
