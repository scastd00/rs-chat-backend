package rs.chat.utils;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import rs.chat.domain.entity.User;

import static java.util.Collections.emptySet;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static rs.chat.utils.TestConstants.FAKER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
	public static User createUserWithRole(String role) {
		String name = FAKER.name().username();
		String fullName = name.replace(".", "");
		String username = name.split("\\.")[0] + RandomStringUtils.randomAlphanumeric(4);
		String email = username + "@hello.com";
		String code = RandomStringUtils.randomAlphanumeric(6);
		String password = "!PasswordSpecialChars_123$";

		return new User(
				null, username, password, email,
				fullName, (byte) 21, null, role,
				null, code, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);
	}

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
