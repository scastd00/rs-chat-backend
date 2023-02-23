package rs.chat.utils;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static rs.chat.utils.TestConstants.TEST_GSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
	/**
	 * Configures a new {@link MockHttpServletRequestBuilder} with the given {@link HttpMethod} and URL template.
	 * Also, assigns the security context, the CSRF token to the request and sets the request to be secure (HTTPS).
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
		                             .with(csrf())
		                             .secure(true);
	}

	/**
	 * Parses the given JSON string into a {@link JsonObject}.
	 *
	 * @param jsonString the JSON string to parse.
	 *
	 * @return the parsed {@link JsonObject}.
	 *
	 * @apiNote Same as the production one, but used inside tests.
	 */
	public static JsonObject parseJson(String jsonString) {
		return TEST_GSON.fromJson(jsonString, JsonObject.class);
	}
}
