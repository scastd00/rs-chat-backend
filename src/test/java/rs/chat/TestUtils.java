package rs.chat;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import rs.chat.domain.entity.User;

import static java.util.Collections.emptySet;
import static rs.chat.Constants.FAKER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
	public static User createUserWithRole(String role) {
		String fullName = FAKER.name().fullName();
		String username = fullName.split(" ")[0].toLowerCase();
		String email = username + "@hello.com";
		String code = RandomStringUtils.randomAlphanumeric(6);
		String password = RandomStringUtils.randomAlphanumeric(30);

		return new User(
				null, username, password, email,
				fullName, (byte) 21, null, role,
				null, code, new JsonObject(), emptySet(),
				emptySet(), emptySet(), emptySet(), emptySet(),
				emptySet(), emptySet()
		);
	}
}
