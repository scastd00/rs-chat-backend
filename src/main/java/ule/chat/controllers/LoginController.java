package ule.chat.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ule.chat.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static ule.chat.utils.Utils.typeToken;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {


	@PostMapping("/login")
	public void login(HttpServletRequest request) {
		Object tokens = request.getAttribute("USER:TOKENS");
		request.removeAttribute("USER:TOKENS");

		Map<String, String> tokenMap = Constants.GSON.fromJson(tokens.toString(), typeToken());
	}
}
