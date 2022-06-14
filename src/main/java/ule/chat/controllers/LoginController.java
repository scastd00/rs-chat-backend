package ule.chat.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {
	@PostMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response) {
		log.info("Hola 1, {}", request.getAttribute("SESSION:TOKEN"));
	}
}
