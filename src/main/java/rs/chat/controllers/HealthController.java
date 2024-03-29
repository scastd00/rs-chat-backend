package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
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
	 * Returns the status of the application.
	 *
	 * @param res the response object to send the status.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	@GetMapping(HEALTH_URL)
	public void status(HttpServletResponse res) throws IOException {
		new HttpResponse(res).sendStatus(OK);
	}
}
