package rs.chat.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
	/**
	 * Configures a new {@link MockHttpServletRequestBuilder} with the given {@link HttpMethod} and URL template.
	 * Also, assigns the security context and CSRF token to the request.
	 *
	 * @param method       the HTTP method to use.
	 * @param urlTemplate  the URL template.
	 * @param urlVariables the URL variables.
	 *
	 * @return the configured {@link MockHttpServletRequestBuilder}.
	 */
	public static MockHttpServletRequestBuilder request(HttpMethod method, String urlTemplate, Object... urlVariables) {
		return MockMvcRequestBuilders.request(method, urlTemplate, urlVariables)
		                             .with(testSecurityContext())
		                             .with(csrf());
	}
}
