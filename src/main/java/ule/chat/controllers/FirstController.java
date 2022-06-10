package ule.chat.controllers;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ule.chat.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static ule.chat.utils.Constants.gson;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class FirstController {
	@GetMapping("/hello")
	public String login() {
		return "<h1>Hello World!</h1>";
	}

	@PostMapping("/hello")
	public void post(@RequestBody Map<String, String> text) {
		log.info("{}", text);
		log.info("{}", text.get("text"));
	}
}
