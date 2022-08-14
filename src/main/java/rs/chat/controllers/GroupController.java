package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.Group;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.GroupService;

import java.io.IOException;
import java.util.List;

import static rs.chat.router.Routes.GetRoute.GROUPS_URL;
import static rs.chat.router.Routes.PostRoute.GROUP_SAVE_URL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupController {
	private final GroupService groupService;

	@GetMapping(GROUPS_URL)
	public void getAllGroups(HttpResponse response) throws IOException {
		List<Group> groups = this.groupService.getAll();
		response.ok().send("groups", groups);
	}

	@PostMapping(GROUP_SAVE_URL)
	public void saveGroup(HttpRequest request, HttpResponse response) throws IOException {
		String groupName = request.body().get("name").getAsString();

		Group savedGroup = this.groupService.saveGroup(new Group(null, groupName));

		response.created(GROUP_SAVE_URL).send("data", savedGroup);
	}
}
