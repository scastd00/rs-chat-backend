package ule.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ule.chat.router.Routes;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
	@RequestMapping(Routes.ROOT_URL)
	public String frontend() {
		return "redirect:index.html";
	}
}
