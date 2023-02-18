package rs.chat.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rs.chat.config.security.JWTService;
import rs.chat.config.security.filter.AuthenticationFilter;
import rs.chat.config.security.filter.AuthorizationFilter;
import rs.chat.controllers.BadgeController;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.entity.mappers.BadgeMapperImpl;
import rs.chat.domain.service.BadgeService;
import rs.chat.domain.service.SessionService;

@WebMvcTest(BadgeController.class)
class IntegrationTest {
	private MockMvc mvc;
	@MockBean private BadgeService badgeService;
	private static BadgeMapper badgeMapper;
	@MockBean private JWTService jwtService;
	@MockBean private SessionService sessionService;
	@MockBean private AuthenticationManager authenticationManager;

	@BeforeAll
	static void setUp() {
		badgeMapper = new BadgeMapperImpl();
	}

	@BeforeEach
	void setUpEach() {
		AuthorizationFilter authorizationFilter = new AuthorizationFilter(jwtService, sessionService);
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, jwtService);

		this.mvc = MockMvcBuilders.standaloneSetup(new BadgeController(badgeService))
		                          .addFilters(authorizationFilter, authenticationFilter)
		                          .build();
	}

	@Test
	void getBadgesOfUser() throws Exception {
//		// Given
//		given(badgeService.getBadgesOfUser(1L))
//				.willReturn(Stream.of(
//						Badge.builder().id(1L).title("Badge 1").build(),
//						Badge.builder().id(2L).title("Badge 2").build()
//				).map(badgeMapper::toDto).toList());
//
//		// When
//		MockHttpServletResponse response = mvc.perform(get(USER_BADGES_URL, 1L))
//		                                      .andExpect(status().isOk())
//		                                      .andReturn()
//		                                      .getResponse();
//
//		// Then
//		assertThat(response.getContentAsString()).isEqualTo(
//				objectMapper.writeValueAsString(Badge.builder().id(1L).title("Badge 1").build())
//						+ objectMapper.writeValueAsString(Badge.builder().id(2L).title("Badge 2").build())
//		);
	}
}
