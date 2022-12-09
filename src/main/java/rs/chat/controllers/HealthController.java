package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.GetRoute.HEALTH_URL;

/**
 * Controller to check the status of the application.
 * It is public and does not require authentication.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthController {
	/**
	 * Check the status of the application.
	 */
	@GetMapping(HEALTH_URL)
	public void status(HttpResponse response) throws IOException {
		log.info("Health check");
		response.status(OK).send("Health OK");
	}
}
