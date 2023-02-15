package rs.chat.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.entity.mappers.BadgeMapperImpl;
import rs.chat.domain.service.BadgeService;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.Constants.TEST_TOKEN;
import static rs.chat.router.Routes.GetRoute.USER_BADGES_URL;

@AutoConfigureJsonTesters
@WebMvcTest(BadgeController.class)
class BadgeControllerTest {
	@Autowired private MockMvc mvc;
	@MockBean private BadgeService badgeService;
	@Autowired private JacksonTester<Badge> jsonBadge;
	private static BadgeMapper badgeMapper;

	@BeforeAll
	static void setUp() {
		badgeMapper = new BadgeMapperImpl();
	}

	@Test
	void getBadgesOfUser() throws Exception {
		// Given
		given(badgeService.getBadgesOfUser(1L))
				.willReturn(Stream.of(
						Badge.builder().id(1L).title("Badge 1").build(),
						Badge.builder().id(2L).title("Badge 2").build()
				).map(badgeMapper::toDto).toList());

		// When
		MockHttpServletResponse response = mvc.perform(get(USER_BADGES_URL, 1L)
				                                               .header(AUTHORIZATION, TEST_TOKEN))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString()).isEqualTo(
				jsonBadge.write(Badge.builder().id(1L).title("Badge 1").build()).getJson()
						+ jsonBadge.write(Badge.builder().id(2L).title("Badge 2").build()).getJson()
		);
	}
}
