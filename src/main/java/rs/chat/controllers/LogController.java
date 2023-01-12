package rs.chat.controllers;

import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static rs.chat.router.Routes.GetRoute.LOGS_URL;

/**
 * Controller that manages all logs-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LogController {
	@GetMapping(LOGS_URL)
	public void getAllLogs(HttpResponse response) throws IOException {
		JsonArray logs = new JsonArray();
		Files.readAllLines(Paths.get("logs/rs_chat-app.log"))
		     .forEach(logs::add);
		response.ok().send(logs.toString());
	}
}
