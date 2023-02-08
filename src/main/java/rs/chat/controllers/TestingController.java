package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
		response.ok().send(example1());
	}

	@NotNull
	private JsonObject example1() {
		JsonObject content = new JsonObject();
		content.addProperty("string", "test");
		content.addProperty("int", 1);
		content.addProperty("double", 1.1);
		content.addProperty("boolean", true);
		content.addProperty("character", 'c');
		content.add("null", JsonNull.INSTANCE);

		JsonObject other = new JsonObject();
		other.addProperty("string", "test2");
		other.addProperty("int", 2);
		other.addProperty("double", 2.2);
		other.addProperty("boolean", false);
		other.addProperty("character", 'h');
		other.add("null", JsonNull.INSTANCE);

		JsonArray array = new JsonArray();
		array.add("test");
		array.add(1);
		array.add(1.1);
		array.add(true);
		array.add('c');
		array.add(JsonNull.INSTANCE);

		other.add("array", array);

		content.add("object", other);
		return content;
	}
}
