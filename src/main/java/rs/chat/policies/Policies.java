package rs.chat.policies;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.InvalidPasswordException;
import rs.chat.exceptions.MaliciousCodeInjectionException;
import rs.chat.exceptions.MinimumRequirementsNotMetException;

import java.util.ArrayList;

public final class Policies {
	private Policies() {
	}

	private static final ArrayList<String> excludedCharacters = new ArrayList<>();

	static {
		excludedCharacters.add("\"");
		excludedCharacters.add("'");
		excludedCharacters.add("--");
		excludedCharacters.add("=");
	}

	public static void checkRegister(JsonObject body) {
		String username = getIfPresent(body, "username").getAsString().trim();
		String fullName = getIfPresent(body, "fullName").getAsString().trim();
		String password = getIfPresent(body, "password").getAsString().trim();
		String email = getIfPresent(body, "email").getAsString().trim();
		String confirmPassword = getIfPresent(body, "confirmPassword").getAsString().trim();
		boolean agreeTerms = getIfPresent(body, "agreeTerms").getAsBoolean();

		if (!agreeTerms) {
			throw new BadRequestException("You must accept the terms and conditions before using the app.");
		}

		if (username.length() < 5 || username.length() > 15) {
			throw new BadRequestException("Username must contain between 5 and 15 characters.");
		}

		if (fullName.length() > 100) {
			throw new BadRequestException("Full name must be shorter than 100 characters.");
		}

		if (containsSQLKeywords(fullName)) {
			throw new MaliciousCodeInjectionException("Full name", excludedCharacters);
		}

		if (!email.matches("^[^@]+@[^@]+\\.[^@]{2,}$")) {
			throw new BadRequestException("Email must have a valid structure. Eg: hello@domain.com");
		}

		if (containsSQLKeywords(password)) {
			throw new MaliciousCodeInjectionException("Password", excludedCharacters);
		}

		if (password.length() < 8) {
			throw new InvalidPasswordException("Password is too simple, please provide 8 or more characters (up to 28).");
		}

		if (password.length() > 28) {
			throw new InvalidPasswordException("Password is too long. It must be between 8 and 28 characters.");
		}

		if (!password.equals(confirmPassword)) {
			throw new InvalidPasswordException("Passwords do not match.");
		}
	}

	private static boolean containsSQLKeywords(String password) {
		return excludedCharacters.stream().anyMatch(password::contains);
	}

	private static JsonElement getIfPresent(JsonObject object, String key) {
		if (!object.has(key)) {
			throw new MinimumRequirementsNotMetException(key + " is not present in the request body.");
		}

		return object.get(key);
	}
}
