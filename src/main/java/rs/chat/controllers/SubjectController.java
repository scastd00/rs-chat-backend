package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Subject;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.ChatService;
import rs.chat.service.DegreeService;
import rs.chat.service.SubjectService;

import java.io.IOException;
import java.util.List;

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
	 * @param response response containing all subjects as a List (Array in JSON).
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(SUBJECTS_URL)
	public void getAllSubjects(HttpResponse response) throws IOException {
		List<Subject> allSubjects = this.subjectService.getAll();
		JsonArray subjectsWithInvitationCode = new JsonArray();

		allSubjects.forEach(subject -> {
			JsonObject subjectWithInvitationCode = new JsonObject();
			Chat chat = this.chatService.getByName(subject.getName());

			subjectWithInvitationCode.addProperty("id", subject.getId());
			subjectWithInvitationCode.addProperty("name", subject.getName());
			subjectWithInvitationCode.addProperty("invitationCode", chat.getInvitationCode());
			subjectsWithInvitationCode.add(subjectWithInvitationCode);
		});

		response.ok().send("subjects", subjectsWithInvitationCode.toString());
	}

	/**
	 * Saves a new subject to db.
	 *
	 * @param request  request containing parameters of the new subject.
	 * @param response response containing saved subject.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(SUBJECT_SAVE_URL)
	public void saveSubject(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		String name = body.get("name").getAsString();
		String period = body.get("period").getAsString();
		String type = body.get("type").getAsString();
		Byte credits = body.get("credits").getAsByte();
		Byte grade = body.get("grade").getAsByte();
		String degree = body.get("degree").getAsString();

		if (this.subjectService.exists(name)) {
			throw new BadRequestException("Subject '%s' already exists.".formatted(name));
		}

		Subject savedSubject = this.subjectService.save(
				new Subject(
						null,
						name,
						period,
						type,
						credits,
						grade,
						this.degreeService.getByName(degree).getId()
				)
		);

		response.created(SUBJECT_SAVE_URL).send("subject", savedSubject);
	}
}
