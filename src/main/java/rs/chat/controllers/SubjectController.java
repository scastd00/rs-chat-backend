package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.domain.Subject;
import rs.chat.exceptions.BadRequestException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.DegreeService;
import rs.chat.service.SubjectService;

import java.io.IOException;
import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.GetRoute.SUBJECTS_URL;
import static rs.chat.router.Routes.PostRoute.SUBJECT_SAVE_URL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SubjectController {
	private final SubjectService subjectService;
	private final DegreeService degreeService;

	@GetMapping(SUBJECTS_URL)
	public void getAllSubjects(HttpResponse response) throws IOException {
		response.status(OK)
		        .send("subjects", this.subjectService.getAll());
	}

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

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(SUBJECT_SAVE_URL)
		                                                .toUriString());
		response.setHeader("Location", uri.toString());
		response.status(CREATED).send("subject", savedSubject);
	}
}
