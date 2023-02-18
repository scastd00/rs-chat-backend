package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Group;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.GroupService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.DeleteRoute.DELETE_GROUP_URL;
import static rs.chat.router.Routes.GetRoute.GROUPS_URL;
import static rs.chat.router.Routes.PostRoute.GROUP_SAVE_URL;

/**
 * Controller that manages all group-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupController {
	private final GroupService groupService;
	private final ChatService chatService;

	/**
	 * Returns all groups stored in db.
	 *
	 * @param res response containing all groups as a List (Array in JSON).
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(GROUPS_URL)
	public void getAllGroups(HttpServletResponse res) throws IOException {
		List<Group> groups = this.groupService.getAll();
		JsonArray groupsWithInvitationCode = new JsonArray();

		groups.stream()
		      .map(this::getGroupWithInvitationCode)
		      .forEach(groupsWithInvitationCode::add);

		new HttpResponse(res).ok().send(groupsWithInvitationCode);
	}

	/**
	 * Saves a new group to db.
	 *
	 * @param req request containing new group's name.
	 * @param res response containing saved group.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(GROUP_SAVE_URL)
	public void saveGroup(HttpServletRequest req, HttpServletResponse res) throws IOException {
		HttpRequest request = new HttpRequest(req);
		String groupName = request.body().get("name").getAsString();
		Group savedGroup = this.groupService.saveGroup(new Group(null, groupName, emptySet()));

		new HttpResponse(res).created(GROUP_SAVE_URL).send(this.getGroupWithInvitationCode(savedGroup));
	}

	/**
	 * Deletes given group from db.
	 *
	 * @param res response (does not contain the deleted group, only status code
	 *            is returned to user).
	 * @param id  id of the group to be deleted.
	 *
	 * @throws IOException if an error occurs.
	 */
	@DeleteMapping(DELETE_GROUP_URL)
	public void deleteGroup(HttpServletResponse res, @PathVariable Long id) throws IOException {
		this.groupService.deleteById(id);
		new HttpResponse(res).sendStatus(OK);
	}

	/**
	 * Returns a JsonObject containing group's name, id and invitation code.
	 *
	 * @param group group to be converted.
	 *
	 * @return JsonObject containing group's name, id and invitation code.
	 */
	@NotNull
	private JsonObject getGroupWithInvitationCode(Group group) {
		JsonObject groupWithInvitationCode = new JsonObject();

		groupWithInvitationCode.addProperty("id", group.getId());
		groupWithInvitationCode.addProperty("name", group.getName());
		groupWithInvitationCode.addProperty("invitationCode", this.chatService.getInvitationCodeByChatName(group.getName()));

		return groupWithInvitationCode;
	}
}
