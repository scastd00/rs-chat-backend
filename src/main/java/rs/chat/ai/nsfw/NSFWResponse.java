package rs.chat.ai.nsfw;

import com.google.gson.JsonObject;

/**
 * Represents the response from the NSFW API.
 *
 * @param predictions The predictions from the NSFW API.
 */
public record NSFWResponse(JsonObject predictions) {
	/**
	 * Represents the classification classes returned by the NSFW API.
	 */
	public enum NSFWClass {
		NEUTRAL,
		DRAWINGS,
		SEXY,
		HENTAI,
		PORN
	}

	/**
	 * Returns the probability of the given classification class. If the given
	 * classification class is not found, 0.0 is returned.
	 *
	 * @param nsfwClass The classification class to get the probability of.
	 *
	 * @return The probability of the given classification class.
	 *
	 * @implNote The NSFW API returns all the responses in lowercase, and all the
	 * enum values are contained in it, so we can just convert the enum
	 * value to lowercase and use it as a key.
	 */
	public double probabilityOfClass(NSFWClass nsfwClass) {
		for (NSFWClass value : NSFWClass.values()) {
			String predictionName = value.name().toLowerCase();
			double probability = this.predictions.get(predictionName).getAsDouble();

			if (value.equals(nsfwClass)) {
				return probability;
			}
		}

		return 0.0;
	}
}
