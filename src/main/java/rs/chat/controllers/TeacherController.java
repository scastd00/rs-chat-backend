package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.service.TeacherService;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.GetRoute.TEACHER_SUBJECTS_URL;

/**
 * Controller for the teacher functionality.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TeacherController {
	private final TeacherService teacherService;

	@GetMapping(TEACHER_SUBJECTS_URL)
	public void getSubjects(HttpResponse response, @PathVariable Long id) throws IOException {
		log.info("Getting subjects for teacher with id: {}", id);
		response.status(OK).send(this.teacherService.getSubjects(id));
	}
}
