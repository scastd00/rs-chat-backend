package rs.chat.policies;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.constraint.CharSequenceConstraint;
import am.ik.yavi.constraint.password.CharSequencePasswordPoliciesBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import am.ik.yavi.core.ViolationMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.InvalidPasswordException;
import rs.chat.exceptions.MaliciousCodeInjectionException;
import rs.chat.exceptions.MinimumRequirementsNotMetException;

import java.util.ArrayList;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Policies {
	private static final ArrayList<String> excludedCharacters = new ArrayList<>();

	static {
		excludedCharacters.add("\"");
		excludedCharacters.add("'");
		excludedCharacters.add("--");
		excludedCharacters.add("=");
	}

	private static final String EMAIL_KEY = "email";
	private static final String USERNAME_KEY = "username";
	private static final String FULL_NAME_KEY = "fullName";
	private static final String PASSWORD_KEY = "password";
	private static final String CONFIRM_PASSWORD_KEY = "confirmPassword";
	private static final String AGREE_TERMS_KEY = "agreeTerms";
	private static final String EQUAL_PASSWORDS_KEY = "equalPasswords";

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
		Validator<JsonObject> validator = ValidatorBuilder
				.<JsonObject>of()
				._boolean(o -> get(o, AGREE_TERMS_KEY).getAsBoolean(),
				          AGREE_TERMS_KEY,
				          c -> c.isTrue()
				                .message("You must agree to the terms and conditions."))
				.nest(o -> o, EMAIL_KEY, emailValidator())
				._string(o -> get(o, USERNAME_KEY).getAsString().trim(),
				         USERNAME_KEY,
				         c -> c.notBlank()
				               .greaterThanOrEqual(5)
				               .lessThanOrEqual(15)
				               .pattern("^[a-zA-Z0-9_]+$")
				               .message("The username can only contain letters, numbers and underscores. Must be " +
						                        "between 5 and 15 characters."))
				._string(o -> get(o, FULL_NAME_KEY).getAsString().trim(),
				         FULL_NAME_KEY,
				         c -> c.notBlank()
				               .lessThanOrEqual(100)
				               .predicate(
						               Predicate.not(Policies::containsSQLKeywords),
						               ViolationMessage.of(FULL_NAME_KEY, "\"{0}\" contains malicious code"))
				               .message("The full name can only contain letters, numbers and underscores. " +
						                        "Must have less than 100 characters."))
				.nest(o -> o, "passwords", passwordValidator())
				.failFast(true)
				.build();

		ConstraintViolations violations = validator.validate(body);

		if (violations.isValid()) {
			return;
		}

		String name = violations.get(0).name(); // Since we are using failFast, we only need to check the first violation.

		if (name.equals(PASSWORD_KEY) || name.equals(CONFIRM_PASSWORD_KEY) || name.equals(EQUAL_PASSWORDS_KEY)) {
			throw new InvalidPasswordException(violations.get(0).message());
		}

		throw new MinimumRequirementsNotMetException(violations.get(0).message());
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
	 * @throws IllegalArgumentException if the given key is not found in the given {@link JsonObject} object.
	 */
	private static JsonElement get(JsonObject json, String key) {
		if (!json.has(key)) {
			throw new IllegalArgumentException(key + " is not present in the request body.");
		}

		return json.get(key);
	}

	/**
	 * Checks if the given user contains valid passwords to change them.
	 *
	 * @param body the body of the request that contains the user.
	 *
	 * @throws InvalidPasswordException           if the passwords don't match.
	 * @throws MinimumRequirementsNotMetException if the password is too short or too long.
	 */
	public static void checkPasswords(JsonObject body) {
		ConstraintViolations violations = passwordValidator().validate(body);

		if (violations.isValid()) {
			return;
		}

		if (violations.get(0).name().equals(EQUAL_PASSWORDS_KEY)) {
			throw new InvalidPasswordException(violations.get(0).message());
		}

		throw new MinimumRequirementsNotMetException(violations.get(0).message());
	}

	/**
	 * Checks if the email contained in the body is valid.
	 *
	 * @param body JsonObject containing the email.
	 *
	 * @throws MinimumRequirementsNotMetException if the email is not valid.
	 */
	public static void checkEmail(JsonObject body) {
		if (!emailValidator().validate(body).isValid()) {
			throw new MinimumRequirementsNotMetException("Email must have a valid structure. Eg: hello@domain.com");
		}
	}

	/**
	 * Validator to check passwords follow the following rules:
	 * <ul>
	 *     <li>Must not be blank.</li>
	 *     <li>Must be between 8 and 28 characters long.</li>
	 *     <li>Must be a strong password (see {@link CharSequencePasswordPoliciesBuilder#strong()}).</li>
	 *     <li>Must not contain any SQL keywords.</li>
	 *     <li>Must be equal to the confirmation password.</li>
	 * </ul>
	 *
	 * @return a {@link Validator} that validates the password, the confirmation password and the
	 * equality of the two.
	 */
	private static Validator<JsonObject> passwordValidator() {
		return ValidatorBuilder
				.<JsonObject>of()
				._string(o -> get(o, PASSWORD_KEY).getAsString().trim(),
				         PASSWORD_KEY,
				         c -> c.notBlank()
				               .password(CharSequencePasswordPoliciesBuilder::strong)
				               .greaterThanOrEqual(8)
				               .lessThanOrEqual(28)
				               .predicate(
						               Predicate.not(Policies::containsSQLKeywords),
						               ViolationMessage.of(PASSWORD_KEY, "\"{0}\" contains malicious code"))
				               .message("The password must contain at least one uppercase letter, one lowercase " +
						                        "letter, one number and one special character. It must be " +
						                        "between 8 and 28 characters."))
				._string(o -> get(o, CONFIRM_PASSWORD_KEY).getAsString().trim(),
				         CONFIRM_PASSWORD_KEY,
				         c -> c.notBlank()
				               .greaterThanOrEqual(8)
				               .lessThanOrEqual(28)
				               .message("Confirmation password must have between 8 and 28 characters."))
				._boolean(o -> {
					          String password = get(o, PASSWORD_KEY).getAsString().trim();
					          String confirmPassword = get(o, CONFIRM_PASSWORD_KEY).getAsString().trim();
					          return password.equals(confirmPassword);
				          },
				          EQUAL_PASSWORDS_KEY,
				          c -> c.isTrue()
				                .message("Passwords do not match."))
				.build();
	}

	/**
	 * @return a {@link Validator} that checks if the email fulfills the rules specified in
	 * {@link CharSequenceConstraint#email()}
	 */
	private static Validator<JsonObject> emailValidator() {
		return ValidatorBuilder
				.<JsonObject>of()
				._string(o -> get(o, EMAIL_KEY).getAsString().trim(), EMAIL_KEY,
				         c -> c.notBlank()
				               .email()
				               .message("The email is invalid."))
				.build();
	}
}
