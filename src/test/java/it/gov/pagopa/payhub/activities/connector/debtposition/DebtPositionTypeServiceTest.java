package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeClient;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeServiceTest {

  @Mock
  private AuthnService authnServiceMock;
  @Mock
  private DebtPositionTypeClient debtPositionTypeClientMock;

  private DebtPositionTypeService service;

  private final String accessToken = "ACCESSTOKEN";

  @BeforeEach
  void init() {
    service = new DebtPositionTypeServiceImpl(
        authnServiceMock,
        debtPositionTypeClientMock
    );

    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        authnServiceMock,
        debtPositionTypeClientMock
    );
  }


  @Test
  void givenDebtPositionTypeRequestBodyWhenCreateDebtPositionTypeThenReturnDebtPositionTypeType() {
    // Given
    DebtPositionTypeRequestBody requestBody = new DebtPositionTypeRequestBody();
    DebtPositionType expectedDebtPositionType = new DebtPositionType();
    Mockito.when(debtPositionTypeClientMock.createDebtPositionType(requestBody, accessToken))
        .thenReturn(expectedDebtPositionType);

    // When
    DebtPositionType result = service.createDebtPositionType(requestBody);

    // Then
    Assertions.assertSame(expectedDebtPositionType, result);
  }

  @Test
  void givenMainFieldsWhenGetByMainFieldsThenReturnCollectionModel() {
    // Given
    String code = "CODE";
    Long brokerId = 123L;
    String orgType = "ORG_TYPE";
    String macroArea = "MACRO";
    String serviceType = "SERVICE";
    String collectingReason = "REASON";
    String taxonomyCode = "TAX";
    var expected = Mockito.mock(CollectionModelDebtPositionType.class);
    Mockito.when(debtPositionTypeClientMock.getByMainFields(code, brokerId, orgType, macroArea, serviceType, collectingReason, taxonomyCode, accessToken))
        .thenReturn(expected);

    // When
    var result = service.getByMainFields(code, brokerId, orgType, macroArea, serviceType, collectingReason, taxonomyCode);

    // Then
    Assertions.assertSame(expected, result);
  }

  @Test
  void givenBrokerIdAndCodeWhenGetByBrokerIdAndCodeThenReturnCollectionModel() {
    // Given
    String code = "CODE";
    Long brokerId = 123L;
    var expected = Mockito.mock(CollectionModelDebtPositionType.class);
    Mockito.when(debtPositionTypeClientMock.getByBrokerIdAndCode(brokerId, code, accessToken))
            .thenReturn(expected);

    // When
    var result = service.getByBrokerIdAndCode(brokerId, code);

    // Then
    Assertions.assertSame(expected, result);
  }

}
