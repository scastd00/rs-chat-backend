package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.ChatService;
import rs.chat.service.GroupService;

import java.io.IOException;
import java.util.List;

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
	 * @param response response containing all groups as a List (Array in JSON).
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(GROUPS_URL)
	public void getAllGroups(HttpResponse response) throws IOException {
		List<Group> groups = this.groupService.getAll();
		JsonArray groupsWithInvitationCode = new JsonArray();

		groups.forEach(group -> {
			JsonObject groupWithInvitationCode = new JsonObject();
			Chat chat = this.chatService.getByName(group.getName());

			groupWithInvitationCode.addProperty("id", group.getId());
			groupWithInvitationCode.addProperty("name", group.getName());
			groupWithInvitationCode.addProperty("invitationCode", chat.getInvitationCode());
			groupsWithInvitationCode.add(groupWithInvitationCode);
		});

		response.ok().send("groups", groupsWithInvitationCode.toString());
	}

	/**
	 * Saves a new group to db.
	 *
	 * @param request  request containing new group's name.
	 * @param response response containing saved group.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(GROUP_SAVE_URL)
	public void saveGroup(HttpRequest request, HttpResponse response) throws IOException {
		String groupName = request.body().get("name").getAsString();

		Group savedGroup = this.groupService.saveGroup(new Group(null, groupName));

		response.created(GROUP_SAVE_URL).send("data", savedGroup);
	}
}
