package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public void getDto(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		response.getWriter().write("Hello world!");
//		response.ok().send("Hello world!");
	}
}
