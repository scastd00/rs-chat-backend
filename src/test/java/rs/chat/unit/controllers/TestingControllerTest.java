package rs.chat.unit.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.TestingController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.router.Routes.TEST_URL;

@AutoConfigureJsonTesters
@WebMvcTest(TestingController.class)
class TestingControllerTest {
	@Autowired private MockMvc mvc;

	@Test
	@WithMockUser
	void test() throws Exception {
		MockHttpServletResponse response = mvc.perform(get(TEST_URL))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		assertThat(response.getContentAsString()).isEqualTo("Hello world!");
	}
}
