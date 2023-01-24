package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
	@GetMapping(TEST_URL)
	public void getDto(HttpResponse response) throws IOException {
		response.ok().send();
	}
}
