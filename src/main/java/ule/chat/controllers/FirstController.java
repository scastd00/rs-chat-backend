package ule.chat.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class FirstController {
	@GetMapping("/login")
	public String login() {
		return "<h1>Hello World!</h1>";
	}

	@PostMapping("/login")
	public void post(@RequestBody Map<String, String> text) {
		log.info("{}", text);
		log.info("{}", text.get("text"));
	}
}
