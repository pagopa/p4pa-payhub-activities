package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIuvActivityImplTest {
	private static final Long ORGANIZATION = 1L;
	private static final String IUV = "01011112222333345";

	@Mock
	private ClassificationDao classificationDaoMock;

	private ClearClassifyIuvActivity activity;

	@BeforeEach
	void setUp() {
		activity = new ClearClassifyIuvActivityImpl(classificationDaoMock);
	}

	@Test
	void deleteClassificationSuccess() {
		assertDoesNotThrow(() -> activity.deleteClassificationByIuv(ORGANIZATION, IUV));
	}

	@Test
	void deleteClassificationFailed() {
		when(classificationDaoMock.deleteClassificationByIuv(ORGANIZATION, IUV)).thenReturn(Boolean.FALSE);
		assertFalse(activity.deleteClassificationByIuv(ORGANIZATION, IUV));
	}
}
