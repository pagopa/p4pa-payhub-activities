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
class TransferClassificationActivityImplTest {
	private static final Long ORGANIZATION = 123L;
	private static final String IUV = "01011112222333345";
	private static final String IUR = "IUR";
	private static final int INDEX = 1;

	@Mock
	private ClassificationDao classificationDaoMock;

	private TransferClassificationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new TransferClassificationActivityImpl(classificationDaoMock);
	}

	@Test
	void deleteClassificationSuccess() {
		assertDoesNotThrow(() -> activity.classify(ORGANIZATION, IUV, IUR, INDEX));
	}

	@Test
	void deleteClassificationFailed() {
		when(classificationDaoMock.deleteClassificationByTransferKeySet(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.FALSE);
		assertFalse(activity.classify(ORGANIZATION, IUV, IUR, INDEX));
	}
}
