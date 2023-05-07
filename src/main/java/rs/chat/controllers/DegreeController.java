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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Degree;
import rs.chat.domain.entity.mappers.DegreeMapper;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.DegreeService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.DeleteRoute.DELETE_DEGREE_URL;
import static rs.chat.router.Routes.GetRoute.DEGREES_URL;
import static rs.chat.router.Routes.GetRoute.DEGREE_BY_NAME_URL;
import static rs.chat.router.Routes.PostRoute.DEGREE_SAVE_URL;
import static rs.chat.router.Routes.PutRoute.EDIT_DEGREE_NAME_URL;

/**
 * Controller that manages all degree-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DegreeController {
	private final DegreeService degreeService;
	private final ChatService chatService;
	private final DegreeMapper degreeMapper;

	/**
	 * Returns all degrees stored in db.
	 *
	 * @param res response containing all degrees.
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(DEGREES_URL)
	public void getAllDegrees(HttpServletResponse res) throws IOException {
		List<Degree> allDegrees = this.degreeService.getDegrees();
		JsonArray degreesWithInvitationCode = new JsonArray();

		allDegrees.stream()
		          .map(this::getDegreeWithInvitationCodeToChat)
		          .forEach(degreesWithInvitationCode::add);

		new HttpResponse(res).ok().send(degreesWithInvitationCode);
	}

	/**
	 * Returns degree with given name.
	 *
	 * @param res        response containing the degree with given name.
	 * @param degreeName name of the degree to be returned.
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(DEGREE_BY_NAME_URL)
	public void getDegreeByName(HttpServletResponse res, @PathVariable String degreeName) throws IOException {
		HttpResponse response = new HttpResponse(res);

		Degree degree = this.degreeService.getByName(degreeName);

		response.ok().send(this.degreeMapper.toDto(degree));
	}

	/**
	 * Saves given degree to db.
	 *
	 * @param request request containing degree to be saved.
	 * @param res     response containing saved degree.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(DEGREE_SAVE_URL)
	public void saveDegree(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		String degreeName = request.body().get("name").getAsString();

		if (this.degreeService.existsDegree(degreeName)) {
			response.badRequest().send("Degree '%s' already exists".formatted(degreeName));
			log.warn("Degree '{}' already exists", degreeName);
			return;
		}

		Degree degree = this.degreeService.saveDegree(
				new Degree(null, degreeName, emptySet())
		);

		response.created(DEGREE_SAVE_URL).send(this.getDegreeWithInvitationCodeToChat(degree));
	}

	/**
	 * Updates name of the given degree in db.
	 *
	 * @param request request containing degree to be updated.
	 * @param res     response containing updated degree.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PutMapping(EDIT_DEGREE_NAME_URL)
	public void changeDegreeName(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();
		String oldName = body.get("oldName").getAsString();
		String newName = body.get("newName").getAsString();

		Degree degree = this.degreeService.changeDegreeName(oldName, newName);

		response.ok().send(this.degreeMapper.toDto(degree));
	}

	/**
	 * Deletes given degree from db.
	 *
	 * @param res response (does not contain the deleted degree, only status code
	 *            is returned to user).
	 * @param id  id of the degree to be deleted.
	 *
	 * @throws IOException if an error occurs.
	 */
	@DeleteMapping(DELETE_DEGREE_URL)
	public void deleteDegree(HttpServletResponse res, @PathVariable Long id) throws IOException {
		HttpResponse response = new HttpResponse(res);

		this.degreeService.deleteById(id);

		response.sendStatus(OK);
	}

	/**
	 * Returns a JsonObject containing degree's name, id and invitation code.
	 *
	 * @param degree group to be converted.
	 *
	 * @return JsonObject containing degree's name, id and invitation code.
	 */
	@NotNull
	private JsonObject getDegreeWithInvitationCodeToChat(Degree degree) {
		JsonObject degreeWithInvitationCode = new JsonObject();

		degreeWithInvitationCode.addProperty("id", degree.getId());
		degreeWithInvitationCode.addProperty("name", degree.getName());
		degreeWithInvitationCode.addProperty("invitationCode", this.chatService.getInvitationCodeByChatName(degree.getName()));

		return degreeWithInvitationCode;
	}
}
