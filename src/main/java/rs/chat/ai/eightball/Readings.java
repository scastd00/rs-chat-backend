package rs.chat.ai.eightball;

import java.util.Random;

public enum Readings {
	POSITIVE(
			"It is certain", "It is decidedly so", "Without a doubt",
			"Yes, definitely", "You may rely on it", "As I see it, yes",
			"Most likely", "Outlook good", "Yes",
			"Signs point to yes"
	),
	NEUTRAL(
			"Reply hazy, try again", "Ask again later", "Better not tell you now",
			"Cannot predict now", "Concentrate and ask again"
	),
	NEGATIVE(
			"Don't count on it", "My reply is no", "My sources say no",
			"Outlook not so good", "Very doubtful"
	);

	private final String[] strings;

	Readings(String... strings) {
		this.strings = strings;
	}

	public String getRandom(Random random) {
		return this.strings[random.nextInt(this.strings.length)];
	}
}
