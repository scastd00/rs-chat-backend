package rs.chat.net.ws.strategies.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.CommandUnavailableException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static rs.chat.net.ws.strategies.commands.Command.CommandType.ADMIN;
import static rs.chat.net.ws.strategies.commands.Command.CommandType.NORMAL;
import static rs.chat.net.ws.strategies.commands.Command.CommandType.TEACHER;

/**
 * Class that maps commands to their respective strategies.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1192") // Suppress "String literals should not be duplicated" warning
public class StrategyMappings {
	private static final Map<String, Command> commands = new HashMap<>();

	static {
		commands.put("/help", new Command("/help", NORMAL, "Displays a list of all available commands.", "/help", new HelpCommandStrategy()));
		commands.put("/clear", new Command("/clear", NORMAL, "Clears the chat window.", "/clear", new ClearCommandStrategy()));
		commands.put("/me", new Command("/me", NORMAL, "Sends an action message.", "/me <message>", new MeCommandStrategy()));
		commands.put("/nick", new Command("/nick", NORMAL, "Changes your nickname.", "/nick <nickname>", new NickCommandStrategy()));
		commands.put("/join", new Command("/join", NORMAL, "Joins a channel.", "/join <channel>", new JoinCommandStrategy()));
		commands.put("/leave", new Command("/leave", NORMAL, "Leaves a channel.", "/leave <channel>", new LeaveCommandStrategy()));
		commands.put("/msg", new Command("/msg", NORMAL, "Sends a private message to a user.", "/msg <user> <message>", new MsgCommandStrategy()));
		commands.put("/whois", new Command("/whois", NORMAL, "Displays information about a user.", "/whois <user>", new WhoIsCommandStrategy()));
		commands.put("/invite", new Command("/invite", NORMAL, "Invites a user to a channel.", "/invite <user> <channel>", new InviteCommandStrategy()));
		commands.put("/kick", new Command("/kick", TEACHER, "Kicks a user from a channel.", "/kick <user> <channel>", new KickCommandStrategy()));
		commands.put("/ban", new Command("/ban", TEACHER, "Bans a user from a channel.", "/ban <user> <channel>", new BanCommandStrategy()));
		commands.put("/unban", new Command("/unban", TEACHER, "Unbans a user from a channel.", "/unban <user> <channel>", new UnbanCommandStrategy()));
		commands.put("/topic", new Command("/topic", TEACHER, "Changes the topic of a channel.", "/topic <channel> <topic>", new TopicCommandStrategy()));
		commands.put("/mode", new Command("/mode", TEACHER, "Changes the mode of a channel.", "/mode <channel> <mode>", new ModeCommandStrategy()));
		commands.put("/op", new Command("/op", ADMIN, "Gives a user operator status in a channel.", "/op <user> <channel>", new OpCommandStrategy()));
		commands.put("/deop", new Command("/deop", ADMIN, "Removes a user's operator status in a channel.", "/deop <user> <channel>", new DeOpCommandStrategy()));
		commands.put("/ignore", new Command("/ignore", NORMAL, "Ignores a user.", "/ignore <user>", new IgnoreCommandStrategy()));
		commands.put("/unignore", new Command("/unignore", NORMAL, "Unignores a user.", "/unignore <user>", new UnIgnoreCommandStrategy()));
		commands.put("/ignorelist", new Command("/ignorelist", NORMAL, "Displays a list of ignored users.", "/ignorelist", new IgnoreListCommandStrategy()));
		commands.put("/away", new Command("/away", NORMAL, "Sets your status to away.", "/away <message>", new AwayCommandStrategy()));
		commands.put("/back", new Command("/back", NORMAL, "Sets your status to back.", "/back", new BackCommandStrategy()));
		commands.put("/who", new Command("/who", NORMAL, "Displays information about you.", "/who <channel>", new WhoCommandStrategy()));
		commands.put("/names", new Command("/names", NORMAL, "Displays a list of users in a channel.", "/names <channel>", new NamesCommandStrategy()));
		commands.put("/list", new Command("/list", NORMAL, "Displays a list of channels.", "/list", new ListCommandStrategy()));
		commands.put("/time", new Command("/time", NORMAL, "Displays the current time.", "/time", new TimeCommandStrategy()));
		commands.put("/version", new Command("/version", NORMAL, "Displays the version of the server.", "/version", new VersionCommandStrategy()));
		commands.put("/ping", new Command("/ping", NORMAL, "Pings the server.", "/ping", new PingCommandStrategy()));
		commands.put("/quit", new Command("/quit", NORMAL, "Disconnects from the server.", "/quit", new QuitCommandStrategy()));
		commands.put("/dice", new Command("/dice", NORMAL, "Rolls a dice (or challenges another user).", "/dice (<user>)", new DiceCommandStrategy()));
		commands.put("/8ball", new Command("/8ball", NORMAL, "Asks the magic 8-ball a question.", "/8ball <question>", new EightBallCommandStrategy()));
	}

	public static CommandStrategy decideStrategy(String command) {
		return Optional.ofNullable(commands.getOrDefault(command, null))
		               .orElseThrow(() -> new CommandUnavailableException("Command " + command + " is not available."))
		               .strategy();
	}

	public static String getAvailableCommandsWithDescriptionAndUsage() {
		StringBuilder sb = new StringBuilder("##");

		commands.forEach((command, strategy) -> sb.append(command)
		                                          .append(" - ")
		                                          .append(strategy.description())
		                                          .append(" Usage: ")
		                                          .append(strategy.usage())
		                                          .append("##")
		);

		return sb.replace(sb.length() - 2, sb.length(), "").toString();
	}
}
