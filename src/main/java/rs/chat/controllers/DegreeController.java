package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.Degree;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.DegreeService;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.DeleteRoute.DELETE_DEGREE_URL;
import static rs.chat.router.Routes.GetRoute.DEGREES_URL;
import static rs.chat.router.Routes.GetRoute.DEGREE_BY_NAME_URL;
import static rs.chat.router.Routes.PostRoute.DEGREE_SAVE_URL;
import static rs.chat.router.Routes.PutRoute.EDIT_DEGREE_NAME_URL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DegreeController {
	private final DegreeService degreeService;

	@GetMapping(DEGREES_URL)
	public void getAllDegrees(HttpResponse response) throws IOException {
		List<Degree> allDegrees = this.degreeService.getDegrees();
		response.ok().send("degrees", allDegrees);
	}

	@GetMapping(DEGREE_BY_NAME_URL)
	public void getDegreeByName(HttpResponse response,
	                            @PathVariable String degreeName) throws IOException {
		Degree degree = this.degreeService.getByName(degreeName);

		if (degree == null) {
			throw new NotFoundException("degree '%s' not found".formatted(degreeName));
		}

		response.ok().send("degree", degree);
	}

	@PostMapping(DEGREE_SAVE_URL)
	public void saveDegree(HttpRequest request, HttpResponse response) throws IOException {
		String degreeName = request.body().get("name").getAsString();

		if (this.degreeService.existsDegree(degreeName)) {
			throw new BadRequestException("Degree '" + degreeName + "' already exists");
		}

		Degree degree = this.degreeService.saveDegree(
				new Degree(null, degreeName)
		);

		response.created(DEGREE_SAVE_URL).send("degree", degree);
	}

	@PutMapping(EDIT_DEGREE_NAME_URL)
	public void changeDegreeName(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();
		String oldName = body.get("oldName").getAsString();
		String newName = body.get("newName").getAsString();

		if (oldName.equals(newName)) {
			throw new BadRequestException("Names should not be equal");
		}

		Degree degree = this.degreeService.changeDegreeName(oldName, newName);

		response.ok().send("degree", degree);
	}

	@DeleteMapping(DELETE_DEGREE_URL)
	public void deleteDegree(HttpResponse response,
	                         @PathVariable String degreeName) throws IOException {
		if (!this.degreeService.existsDegree(degreeName)) {
			throw new NotFoundException("'%s' does not exist".formatted(degreeName));
		}

		this.degreeService.deleteDegreeByName(degreeName);
		response.sendStatus(OK);
	}
}
