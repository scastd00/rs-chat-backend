package rs.chat.ai.nsfw;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public record NSFWResponse(JsonArray predictionsSortedByProbability) {
	public enum ClassificationClass {
		NEUTRAL,
		DRAWING,
		SEXY,
		HENTAI,
		PORN
	}

	private JsonObject predictionAtPos(int pos) {
		return this.predictionsSortedByProbability.get(pos).getAsJsonObject();
	}

	private String classNameAtPos(int pos) {
		return this.predictionAtPos(pos).get("className").getAsString();
	}

	private double probabilityAtPos(int pos) {
		return this.predictionAtPos(pos).get("probability").getAsDouble();
	}

	private ClassificationClass classAtPos(int pos) {
		return ClassificationClass.valueOf(this.classNameAtPos(pos).toUpperCase());
	}

	public double probabilityOfClass(ClassificationClass classificationClass) {
		for (int i = 0; i < this.predictionsSortedByProbability.size(); i++) {
			if (this.classAtPos(i).equals(classificationClass)) {
				return this.probabilityAtPos(i);
			}
		}

		return 0.0;
	}
}
