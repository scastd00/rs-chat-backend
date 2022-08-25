package rs.chat.policies;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

	/**
	 * Checks if the given user contains valid fields to register a new user.
	 *
	 * @param body the body of the request that contains the user.
	 *
	 * @throws MinimumRequirementsNotMetException if the user does not contain all the necessary fields or they
	 *                                            are incorrect.
	 * @throws MaliciousCodeInjectionException    if the username or passwords contains malicious code.
	 */
	public static void checkRegister(JsonObject body) {
		String username = get(body, "username").getAsString().trim();
		String fullName = get(body, "fullName").getAsString().trim();
		String password = get(body, "password").getAsString().trim();
		String email = get(body, "email").getAsString().trim();
		String confirmPassword = get(body, "confirmPassword").getAsString().trim();
		boolean agreeTerms = get(body, "agreeTerms").getAsBoolean();

		if (!agreeTerms) {
			throw new MinimumRequirementsNotMetException("You must accept the terms and conditions before using the app.");
		}

		if (username.length() < 5 || username.length() > 15) {
			throw new MinimumRequirementsNotMetException("Username must contain between 5 and 15 characters.");
		}

		if (fullName.length() > 100) {
			throw new MinimumRequirementsNotMetException("Full name must be shorter than 100 characters.");
		}

		if (containsSQLKeywords(fullName)) {
			throw new MaliciousCodeInjectionException("Full name", excludedCharacters);
		}

		if (!email.matches("^[^@]+@[^@]+\\.[^@]{2,}$")) {
			throw new MinimumRequirementsNotMetException("Email must have a valid structure. Eg: hello@domain.com");
		}

		if (containsSQLKeywords(password)) {
			throw new MaliciousCodeInjectionException("Password", excludedCharacters);
		}

		checkPasswords(password, confirmPassword);
	}

	/**
	 * Checks if the passwords are valid and if they match.
	 *
	 * @param password        the password of the user.
	 * @param confirmPassword the password confirmation.
	 *
	 * @throws MinimumRequirementsNotMetException if the password is too short or too long.
	 * @throws InvalidPasswordException           if the passwords don't match.
	 */
	private static void checkPasswords(String password, String confirmPassword) {
		if (password.length() < 8) {
			throw new MinimumRequirementsNotMetException("Password is too simple, please provide 8 or more characters (up to 28).");
		}

		if (password.length() > 28) {
			throw new MinimumRequirementsNotMetException("Password is too long. It must be between 8 and 28 characters.");
		}

		if (!password.equals(confirmPassword)) {
			throw new InvalidPasswordException("Passwords do not match.");
		}
	}

	/**
	 * Checks if the {@link String} contains any SQL keywords.
	 *
	 * @param s the {@link String} to check.
	 *
	 * @return true if the {@link String} contains any SQL keywords, false otherwise.
	 */
	private static boolean containsSQLKeywords(String s) {
		return excludedCharacters.stream().anyMatch(s::contains);
	}

	/**
	 * Retrieves the value of the given key from the given {@link JsonObject} object.
	 *
	 * @param json the {@link JsonObject} object to retrieve the value from.
	 * @param key  the key of the value to retrieve.
	 *
	 * @return the value of the given key from the given {@link JsonObject} object.
	 *
	 * @throws MinimumRequirementsNotMetException if the given key is not found in the given {@link JsonObject} object.
	 */
	private static JsonElement get(JsonObject json, String key) {
		if (!json.has(key)) {
			throw new MinimumRequirementsNotMetException(key + " is not present in the request body.");
		}

		return json.get(key);
	}

	/**
	 * Checks if the given user contains valid passwords to change them.
	 *
	 * @param body the body of the request that contains the user.
	 */
	public static void checkPasswords(JsonObject body) {
		String newPassword = get(body, "newPassword").getAsString().trim();
		String confirmPassword = get(body, "confirmPassword").getAsString().trim();

		checkPasswords(newPassword, confirmPassword);
	}
}
