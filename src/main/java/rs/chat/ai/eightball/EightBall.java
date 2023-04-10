package rs.chat.ai.eightball;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Sentiment analysis using Stanford CoreNLP. See <a href="https://stanfordnlp.github.io/CoreNLP/sentiment.html">Stanford Sentiment</a>
 * for more information.
 * <p>
 * The code was based on <a href="https://github.com/crystoll/sentiment-analysis">sentiment-analysis</a> project.
 * </p>
 * <p>
 * The functionality of the 8-ball is to analyze the sentiment of the comments and return a positive or negative answer.
 * It was based on the <a href="https://github.com/jacobschwantes/eight-ball-api">eight-ball-api</a> project.
 */
@Slf4j
public final class EightBall {
	private static final StanfordCoreNLP PIPELINE;
	private static final Random RANDOM = new Random();

	static {
		Properties props = new Properties();
		// Sentiment analysis is only available in English.
		// tokenizer, sentence splitting, consistency parsing, sentiment analysis.
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		PIPELINE = new StanfordCoreNLP(props);
	}

	/**
	 * Record class to hold the sentiment analysis results.
	 *
	 * @param name     Sentiment name. Values are: Very negative, Negative, Neutral, Positive, Very positive.
	 * @param value    Sentiment value. Values range from 0 to 4, where 0 is very negative and 4 is very positive.
	 * @param sentence Sentence. The sentence that was analyzed.
	 */
	record Sentiment(String name, int value, String sentence) {
	}

	public static String getReply(@NotNull String question) {
		List<Sentiment> sentiments = analyze(question);
		Readings readings;

		if (sentiments.stream().anyMatch(EightBall::negativeComments)) {
			readings = Readings.POSITIVE;
		} else if (sentiments.stream().anyMatch(EightBall::positiveComments)) {
			readings = Readings.NEGATIVE;
		} else {
			readings = Readings.NEUTRAL;
		}

		return readings.getRandom(RANDOM);
	}

	private static List<Sentiment> analyze(String content) {
		Annotation annotation = PIPELINE.process(content);

		return annotation.get(CoreAnnotations.SentencesAnnotation.class)
		                 .stream()
		                 .map(EightBall::convertToSentiment)
		                 .toList();
	}

	private static Sentiment convertToSentiment(CoreMap sentence) {
		Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

		return new Sentiment(
				sentence.get(SentimentCoreAnnotations.SentimentClass.class),
				RNNCoreAnnotations.getPredictedClass(tree),
				sentence.toString()
		);
	}

	private static boolean negativeComments(Sentiment sentiment) {
		return sentiment.value < 2;
	}

	private static boolean positiveComments(Sentiment sentiment) {
		return sentiment.value > 2;
	}
}
