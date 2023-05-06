package rs.chat.controllers;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.SubjectService;
import rs.chat.domain.service.TeacherService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.Constants.DEGREE;
import static rs.chat.Constants.SUBJECT;
import static rs.chat.router.Routes.GetRoute.TEACHERS_URL;
import static rs.chat.router.Routes.GetRoute.TEACHER_DEGREES_URL;
import static rs.chat.router.Routes.GetRoute.TEACHER_SUBJECTS_URL;
import static rs.chat.router.Routes.PostRoute.ADD_TEACHER_TO_SUBJECT_URL;

/**
 * Controller for the teacher functionality.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TeacherController {
	private final TeacherService teacherService;
	private final SubjectService subjectService;
	private final ChatService chatService;

	@GetMapping(TEACHERS_URL)
	public void getTeachers(HttpServletResponse res) throws IOException {
		new HttpResponse(res).status(HttpStatus.OK).send(this.teacherService.getTeachers());
	}

	@GetMapping(TEACHER_SUBJECTS_URL)
	public void getTeacherSubjects(HttpServletResponse res, @PathVariable Long id) throws IOException {
		new HttpResponse(res).status(HttpStatus.OK).send(this.teacherService.getSubjects(id));
	}

	@GetMapping(TEACHER_DEGREES_URL)
	public void getTeacherDegrees(HttpServletResponse res, @PathVariable Long id) throws IOException {
		new HttpResponse(res).status(HttpStatus.OK).send(this.teacherService.getDegrees(id));
	}

	@PostMapping(ADD_TEACHER_TO_SUBJECT_URL)
	public void addTeacherToSubject(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();
		long teacherId = body.get("teacherId").getAsLong();
		long subjectId = body.get("subjectId").getAsLong();

		ControllerUtils.performActionThatMayThrowException(response, () -> {
			this.teacherService.addTeacherToSubject(teacherId, subjectId);

			Subject subject = this.subjectService.getById(subjectId);
			Chat chat = this.chatService.getChatByKey(DomainUtils.getChatKey(SUBJECT, Long.toString(subjectId)));
			this.chatService.addUserToChat(teacherId, chat.getId());


			// If the user already belongs to the degree chat, do not add again.
			if (!this.chatService.userAlreadyBelongsToChat(teacherId, subject.getDegree().getId())) {
				chat = this.chatService.getChatByKey(DomainUtils.getChatKey(DEGREE, subject.getDegree().getId().toString()));
				this.chatService.addUserToChat(teacherId, chat.getId());
			}

			return null;
		});

		response.sendStatus(OK);
	}
}
