package rs.chat.policies;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.constraint.BooleanConstraint;
import am.ik.yavi.constraint.password.CharSequencePasswordPoliciesBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.InvalidPasswordException;
import rs.chat.exceptions.MaliciousCodeInjectionException;
import rs.chat.exceptions.MinimumRequirementsNotMetException;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Policies {
	private static final ArrayList<String> excludedCharacters = new ArrayList<>();

	static {
		excludedCharacters.add("\"");
		excludedCharacters.add("'");
		excludedCharacters.add("--");
		excludedCharacters.add("=");
	}

	private static final String emailKey = "email";
	private static final String usernameKey = "username";
	private static final String fullNameKey = "fullName";
	private static final String passwordKey = "password";
	private static final String confirmPasswordKey = "confirmPassword";
	private static final String agreeTermsKey = "agreeTerms";
	private static final String equalPasswordsKey = "equalPasswords";

	/**
	 * Checks if the given user contains valid fields to register a new user.
	 *
	 * @param body the body of the request that contains the user.
	 *
	 * @throws MinimumRequirementsNotMetException if the user does not contain all the necessary fields, or they
	 *                                            are incorrect.
	 * @throws MaliciousCodeInjectionException    if the username or passwords contains malicious code.
	 */
	public static void checkRegister(JsonObject body) {
		String email = get(body, emailKey).getAsString().trim();
		String username = get(body, usernameKey).getAsString().trim();
		String fullName = get(body, fullNameKey).getAsString().trim();
		String password = get(body, passwordKey).getAsString().trim();
		String confirmPassword = get(body, confirmPasswordKey).getAsString().trim();
		boolean agreeTerms = get(body, agreeTermsKey).getAsBoolean();

		Validator<JsonObject> validator = ValidatorBuilder
				.<JsonObject>of()
				._boolean(o -> agreeTerms, agreeTermsKey, BooleanConstraint::isTrue)
				._string(o -> email, emailKey, c -> c.notBlank().email())
				._string(o -> username, usernameKey, c -> c.notBlank().greaterThanOrEqual(5).lessThanOrEqual(15).pattern("^[a-zA-Z0-9_]+$"))
				._string(o -> fullName, fullNameKey, c -> c.notBlank().lessThanOrEqual(100))
				._string(o -> fullName, fullNameKey, c -> c.predicate(Policies::containsSQLKeywords, fullNameKey, "contains malicious code"))
				._string(o -> password, passwordKey, c -> c.notBlank().password(CharSequencePasswordPoliciesBuilder::strong).greaterThanOrEqual(8).lessThanOrEqual(28))
				._string(o -> password, passwordKey, c -> c.predicate(Policies::containsSQLKeywords, passwordKey, "contains malicious code"))
				._string(o -> confirmPassword, confirmPasswordKey, c -> c.notBlank().greaterThanOrEqual(8).lessThanOrEqual(28))
				._boolean(o -> password.equals(confirmPassword), equalPasswordsKey, BooleanConstraint::isTrue)
				.failFast(true)
				.build();

		ConstraintViolations violations = validator.validate(body);

		if (violations.isValid()) {
			return;
		}

		switch (violations.get(0).name()) {
			case agreeTermsKey -> throw new MinimumRequirementsNotMetException("You must agree to the terms and conditions.");
			case emailKey -> throw new MinimumRequirementsNotMetException("The email is invalid.");
			case usernameKey -> throw new MinimumRequirementsNotMetException("The username is invalid.");
			case fullNameKey -> throw new MinimumRequirementsNotMetException("The full name is invalid.");
			case passwordKey -> throw new InvalidPasswordException("The password is invalid.");
			case confirmPasswordKey -> throw new InvalidPasswordException("The confirm password is invalid.");
			case equalPasswordsKey -> throw new InvalidPasswordException("The passwords do not match.");
			default -> throw new MinimumRequirementsNotMetException("The request is invalid.");
		}
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

	public static void passwordValidator(String password) {
		ValidatorBuilder
				.<String>of()
				._string(s -> password, passwordKey, c -> c.notBlank().password(CharSequencePasswordPoliciesBuilder::strong).greaterThanOrEqual(8).lessThanOrEqual(28))
				._string(s -> password, passwordKey, c -> c.predicate(Policies::containsSQLKeywords, passwordKey, "contains malicious code"))
				.build();
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
		String confirmPassword = get(body, confirmPasswordKey).getAsString().trim();

		checkPasswords(newPassword, confirmPassword);
	}

	public static void checkEmail(JsonObject body) {
		String email = get(body, emailKey).getAsString().trim();

		if (!email.matches("^[^@]+@[^@]+\\.[^@]{2,}$")) {
			throw new MinimumRequirementsNotMetException("Email must have a valid structure. Eg: hello@domain.com");
		}
	}
}
