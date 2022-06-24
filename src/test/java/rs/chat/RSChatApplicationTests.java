package rs.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RSChatApplicationTests {
	private final Calculator underTest = new Calculator();

	@Test
	void itShouldAddTwoNumbers() {
		// Given
		int number1 = 10;
		int number2 = 20;

		// When
		int result = this.underTest.add(number1, number2);

		// Then
		int expected = 30;
		assertThat(result).isEqualTo(expected);
	}

	static class Calculator {
		int add(int a, int b) {
			return a + b;
		}
	}
}
