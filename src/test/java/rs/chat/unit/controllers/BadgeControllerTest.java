package rs.chat.unit.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.BadgeController;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.entity.mappers.BadgeMapperImpl;
import rs.chat.domain.service.BadgeService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.router.Routes.GetRoute.USER_BADGES_URL;

@AutoConfigureJsonTesters
@WebMvcTest(BadgeController.class)
class BadgeControllerTest {
	@Autowired private MockMvc mvc;
	private static BadgeMapper badgeMapper;
	@MockBean private BadgeService badgeService;

	@BeforeAll
	static void setUp() {
		badgeMapper = new BadgeMapperImpl();
	}

	@Test
	@WithMockUser
		// This annotation saves putting .with(user(USER_DETAILS)) inside the parameter of perform call
	void getBadgesOfUser() throws Exception {
		// Given
		given(badgeService.getBadgesOfUser(1L))
				.willReturn(List.of(
						badgeMapper.toDto(new Badge(
								1L, "1st message", "First message",
								"/images/badges/1st-message.png", TEXT_MESSAGE.type(), 1,
								Collections.emptySet()
						))
				));

		// When
		MockHttpServletResponse response = mvc.perform(get(USER_BADGES_URL, 1L))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString()).isEmpty();
	}
}
