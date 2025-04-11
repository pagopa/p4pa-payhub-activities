package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIudActivityTest {
	private static final Long ORGANIZATION_ID = 1L;
	private static final String IUD = "IUD";

	@Mock
	private ClassificationService classificationServiceMock;

	private ClearClassifyIudActivity activity;

	@BeforeEach
	void setUp() {
		activity = new ClearClassifyIudActivityImpl(classificationServiceMock);
	}

	@Test
	void whenDeleteClassificationThenOK() {
		assertDoesNotThrow(() -> activity.deleteClassificationByIud(ORGANIZATION_ID, IUD));
	}

	@Test
	void whenDeleteClassificationFailedThenReturnZero() {
		when(classificationServiceMock.deleteByOrganizationIdAndIudAndLabel(ORGANIZATION_ID, IUD, ClassificationsEnum.IUD_NO_RT.name()))
			.thenReturn(0L);
		assertEquals(0L, activity.deleteClassificationByIud(ORGANIZATION_ID, IUD));
	}
}