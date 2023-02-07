package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.service.BadgeService;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static rs.chat.router.Routes.GetRoute.USER_BADGES_URL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BadgeController {
	private final BadgeService badgeService;

	@GetMapping(USER_BADGES_URL)
	public void getBadgesOfUser(HttpResponse response, @PathVariable Long userId) throws IOException {
		response.ok().send(this.badgeService.getBadgesOfUser(userId));
	}
}
