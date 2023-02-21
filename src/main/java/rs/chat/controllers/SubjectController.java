package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.DegreeService;
import rs.chat.domain.service.SubjectService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.DeleteRoute.DELETE_SUBJECT_URL;
import static rs.chat.router.Routes.GetRoute.SUBJECTS_URL;
import static rs.chat.router.Routes.PostRoute.SUBJECT_SAVE_URL;

/**
 * Controller that manages all subject-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SubjectController {
	private final SubjectService subjectService;
	private final DegreeService degreeService;
	private final ChatService chatService;

	/**
	 * Returns all subjects stored in db.
	 *
	 * @param res response containing all subjects as a List (Array in JSON).
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(SUBJECTS_URL)
	public void getAllSubjects(HttpServletResponse res) throws IOException {
		List<Subject> allSubjects = this.subjectService.getAll();
		JsonArray subjectsWithInvitationCode = new JsonArray();

		allSubjects.stream()
		           .map(this::getSubjectWithInvitationCode)
		           .forEach(subjectsWithInvitationCode::add);

		new HttpResponse(res).ok().send(subjectsWithInvitationCode);
	}

	/**
	 * Saves a new subject to db.
	 *
	 * @param request request containing parameters of the new subject.
	 * @param res     response containing saved subject.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(SUBJECT_SAVE_URL)
	public void saveSubject(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();

		String name = body.get("name").getAsString();
		String period = body.get("period").getAsString();
		String type = body.get("type").getAsString();
		Byte credits = body.get("credits").getAsByte();
		Byte grade = body.get("grade").getAsByte();
		String degree = body.get("degree").getAsString(); // Degree name

		if (this.subjectService.exists(name)) {
			response.badRequest().send("Subject '%s' already exists.".formatted(name));
			log.warn("Subject '{}' already exists.", name);
			return;
		}

		Subject savedSubject = this.subjectService.save(
				new Subject(
						null,
						name,
						period,
						type,
						credits,
						grade,
						this.degreeService.getByName(degree),
						emptySet(),
						emptySet()
				)
		);

		response.created(SUBJECT_SAVE_URL).send(this.getSubjectWithInvitationCode(savedSubject));
	}

	/**
	 * Deletes a subject from db.
	 *
	 * @param res response (does not contain the deleted subject, only status code
	 *            is returned to user).
	 * @param id  id of the subject to be deleted.
	 *
	 * @throws IOException if an error occurs.
	 */
	@DeleteMapping(DELETE_SUBJECT_URL)
	public void deleteSubject(HttpServletResponse res, @PathVariable Long id) throws IOException {
		this.subjectService.deleteById(id);
		new HttpResponse(res).sendStatus(OK);
	}

	/**
	 * Returns a JsonObject containing subject's name, id and invitation code.
	 *
	 * @param subject subject to be converted.
	 *
	 * @return JsonObject containing subject's name, id and invitation code.
	 */
	@NotNull
	private JsonObject getSubjectWithInvitationCode(Subject subject) {
		JsonObject subjectWithInvitationCode = new JsonObject();
		Degree degree = this.degreeService.getById(subject.getDegree().getId());

		subjectWithInvitationCode.addProperty("id", subject.getId());
		subjectWithInvitationCode.addProperty("name", "%s (%s)".formatted(subject.getName(), degree.getName()));
		subjectWithInvitationCode.addProperty("invitationCode", this.chatService.getInvitationCodeByChatName(subject.getName()));

		return subjectWithInvitationCode;
	}
}
