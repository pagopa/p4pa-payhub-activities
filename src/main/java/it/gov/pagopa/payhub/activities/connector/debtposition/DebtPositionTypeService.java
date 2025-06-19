package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
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


    /**
     * Retrieves a collection of debt position types filtered by the specified main fields.
     *
     * @param code the code of the debt position type
     * @param brokerId the broker identifier
     * @param orgType the organization type
     * @param macroArea the macro area
     * @param serviceType the service type
     * @param collectingReason the collecting reason
     * @param taxonomyCode the taxonomy code
     * @return a collection of matching DebtPositionType objects
     */
    CollectionModelDebtPositionType getByMainFields(String code, Long brokerId, String orgType,
        String macroArea, String serviceType, String collectingReason, String taxonomyCode);


    /**
     * Retrieves a collection of debt position types filtered by broker ID and code.
     *
     * @param brokerId the broker identifier
     * @param code the code of the debt position type
     * @return a collection of matching DebtPositionType objects
     */
    CollectionModelDebtPositionType getByBrokerIdAndCode(Long brokerId, String code);
}
