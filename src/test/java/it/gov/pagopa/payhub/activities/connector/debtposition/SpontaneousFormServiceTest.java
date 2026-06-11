package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class SpontaneousFormServiceTest {

    @Mock
    private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private SpontaneousFormService spontaneousFormService;

    private final String accessToken = "ACCESSTOKEN";
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        spontaneousFormService = new SpontaneousFormServiceImpl(debtPositionTypeOrgClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            debtPositionTypeOrgClientMock,
            authnServiceMock);
    }

    @Test
    void whenFindByOrganizationIdAndCodeThenInvokeClient() {
        // Given
        Long organizationId = 1L;
        String code = "SF_CODE";
        SpontaneousForm expectedForm = podamFactory.manufacturePojo(SpontaneousForm.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgClientMock.findSpontaneousFormByOrganizationIdAndCode(organizationId, code, accessToken))
            .thenReturn(expectedForm);

        // When
        SpontaneousForm result = spontaneousFormService.findByOrganizationIdAndCode(organizationId, code);

        // Then
        Assertions.assertSame(expectedForm, result);
    }

    @Test
    void whenFindByOrganizationIdAndCodeNotFoundThenReturnNull() {
        // Given
        Long organizationId = 1L;
        String code = "SF_CODE_NOT_FOUND";

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgClientMock.findSpontaneousFormByOrganizationIdAndCode(organizationId, code, accessToken))
            .thenReturn(null);

        // When
        SpontaneousForm result = spontaneousFormService.findByOrganizationIdAndCode(organizationId, code);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenCreateSpontaneousFormThenInvokeClient() {
        // Given
        SpontaneousForm formToCreate = new SpontaneousForm();
        SpontaneousForm createdForm = new SpontaneousForm();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgClientMock.createSpontaneousForm(Mockito.same(formToCreate), Mockito.eq(accessToken)))
            .thenReturn(createdForm);

        // When
        SpontaneousForm result = spontaneousFormService.createSpontaneousForm(formToCreate);

        // Then
        Assertions.assertSame(createdForm, result);
        Assertions.assertEquals(200L, result.getSpontaneousFormId());
    }
}


