package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.ai.eightball.EightBall;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static rs.chat.router.Routes.TEST_URL;

/**
 * Controller that manages all testing-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestingController {
	/**
	 * Returns a simple string.
	 *
	 * @param res The response object.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	@GetMapping(TEST_URL)
	public void test(HttpServletResponse res) throws IOException {
		new HttpResponse(res).ok().send("Hello world!");
	}
}
