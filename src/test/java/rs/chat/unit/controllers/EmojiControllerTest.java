package rs.chat.unit.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(EmojiController.class)
class EmojiControllerTest {

	@Autowired private MockMvc mockMvc;

//	@Test
//	@Disabled
//	void getRandomEmojis() {
//	}
//
//	@Test
//	@Disabled
//	void getEmojisStartingWithString() throws Exception {
//		RequestBuilder request = MockMvcRequestBuilders.get(EMOJI_STARTING_WITH_STRING_URL);
//		MvcResult result = mockMvc.perform(request).andReturn();
//		System.out.println();
//	}
}
