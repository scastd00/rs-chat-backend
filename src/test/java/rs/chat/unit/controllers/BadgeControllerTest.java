package rs.chat.unit.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.BadgeController;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.dtos.BadgeDto;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.entity.mappers.BadgeMapperImpl;
import rs.chat.domain.service.BadgeService;
import rs.chat.utils.security.annotations.WithMockStudent;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.router.Routes.GetRoute.USER_BADGES_URL;
import static rs.chat.utils.TestConstants.TEST_OBJECT_MAPPER;

@WebMvcTest(BadgeController.class)
class BadgeControllerTest {
	@Autowired private MockMvc mvc;

	@MockBean private BadgeService badgeService;

	private final BadgeMapper badgeMapper = new BadgeMapperImpl();

	@Test
	@WithMockStudent
		// This annotation saves putting .with(user(USER_DETAILS)) inside the parameter of perform call
	void itShouldGetBadgesOfUser() throws Exception {
		// Given
		BadgeDto dto1 = badgeMapper.toDto(new Badge(
				1L, "1st message", "First message",
				"/images/badges/1st-message.png", TEXT_MESSAGE.type(), 1,
				Collections.emptySet()
		));
		BadgeDto dto2 = badgeMapper.toDto(new Badge(
				2L, "20th message", "Second message",
				"/images/badges/20th-message.png", TEXT_MESSAGE.type(), 20,
				Collections.emptySet()
		));
		List<BadgeDto> dtoList = List.of(dto1, dto2);
		given(badgeService.getBadgesOfUser(1L)).willReturn(dtoList);

		// When
		MockHttpServletResponse response = mvc.perform(get(USER_BADGES_URL, 1L))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(dtoList));
	}

	@Test
	@WithMockStudent
	void itShouldGetBadgesOfUserWithNoBadges() throws Exception {
		// Given
		List<BadgeDto> dtoList = List.of();
		given(badgeService.getBadgesOfUser(1L)).willReturn(dtoList);

		// When
		MockHttpServletResponse response = mvc.perform(get(USER_BADGES_URL, 1L))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(dtoList));
	}
}
