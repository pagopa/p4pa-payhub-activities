package it.gov.pagopa.payhub.activities.util;

import org.springframework.http.HttpStatusCode;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpTestUtils {

	public static <R> HttpResponse<R> basicHttpOkResponse(R responseBody, HttpHeaders headers, HttpStatusCode status) {
		@SuppressWarnings("unchecked")
		HttpResponse<R> response = mock(HttpResponse.class);
		if(headers != null)
			when(response.headers()).thenReturn(HttpHeaders.of(new HashMap<>(), (_a, _b) -> true));
		if(responseBody != null)
			when(response.body()).thenReturn(responseBody);
		if(status != null)
			when(response.statusCode()).thenReturn(status.value());
		return response;
	}

}
