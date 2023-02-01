package rs.chat.net.ws.strategies.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.CommandUnavailableException;
import rs.chat.net.ws.strategies.commands.impl.AwayCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.BackCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.BanCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.ClearCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.DeOpCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.DiceCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.EightBallCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.FlipCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.HelpCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.IgnoreCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.IgnoreListCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.InviteCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.JoinCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.KickCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.LeaveCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.ListCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.MeCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.ModeCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.MsgCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.NamesCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.NickCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.OpCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.PingCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.QuitCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.TimeCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.TopicCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.UnIgnoreCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.UnbanCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.VersionCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.WhoCommandStrategy;
import rs.chat.net.ws.strategies.commands.impl.WhoIsCommandStrategy;

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
public final class CommandMappings {
	private static final Map<String, Command> commands = new HashMap<>();

	static {
		commands.put(
				"/help",
				new Command("/help", NORMAL, "Displays a list of all available commands.", "/help", new HelpCommandStrategy())
		);
		commands.put(
				"/clear",
				new Command("/clear", NORMAL, "Clears the chat window.", "/clear", new ClearCommandStrategy())
		);
		commands.put(
				"/me",
				new Command("/me", NORMAL, "Sends an action message.", "/me <message>", new MeCommandStrategy(), "message")
		);
		commands.put(
				"/nick",
				new Command("/nick", NORMAL, "Changes your nickname.", "/nick <nickname>", new NickCommandStrategy(), "nickname")
		);
		commands.put(
				"/join",
				new Command("/join", NORMAL, "Joins a channel.", "/join <channel>", new JoinCommandStrategy(), "channel")
		);
		commands.put(
				"/leave",
				new Command("/leave", NORMAL, "Leaves a channel.", "/leave <channel>", new LeaveCommandStrategy(), "channel")
		);
		commands.put(
				"/msg",
				new Command("/msg", NORMAL, "Sends a private message to a user.", "/msg <user> <message>", new MsgCommandStrategy(), "user", "message")
		);
		commands.put(
				"/whois",
				new Command("/whois", NORMAL, "Displays information about a user.", "/whois <user>", new WhoIsCommandStrategy(), "user")
		);
		commands.put(
				"/invite",
				new Command("/invite", NORMAL, "Invites a user to a channel.", "/invite <user> <channel>", new InviteCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/kick",
				new Command("/kick", TEACHER, "Kicks a user from a channel.", "/kick <user> <channel>", new KickCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/ban",
				new Command("/ban", TEACHER, "Bans a user from a channel.", "/ban <user> <channel>", new BanCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/unban",
				new Command("/unban", TEACHER, "Unbans a user from a channel.", "/unban <user> <channel>", new UnbanCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/topic",
				new Command("/topic", TEACHER, "Changes the topic of a channel.", "/topic <channel> <topic>", new TopicCommandStrategy(), "channel", "topic")
		);
		commands.put(
				"/mode",
				new Command("/mode", TEACHER, "Changes the mode of a channel.", "/mode <channel> <mode>", new ModeCommandStrategy(), "channel", "mode")
		);
		commands.put(
				"/op",
				new Command("/op", ADMIN, "Gives a user operator status in a channel.", "/op <user> <channel>", new OpCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/deop",
				new Command("/deop", ADMIN, "Removes a user's operator status in a channel.", "/deop <user> <channel>", new DeOpCommandStrategy(), "user", "channel")
		);
		commands.put(
				"/ignore",
				new Command("/ignore", NORMAL, "Ignores a user.", "/ignore <user>", new IgnoreCommandStrategy(), "user")
		);
		commands.put(
				"/unignore",
				new Command("/unignore", NORMAL, "Unignores a user.", "/unignore <user>", new UnIgnoreCommandStrategy(), "user")
		);
		commands.put(
				"/ignorelist",
				new Command("/ignorelist", NORMAL, "Displays a list of ignored users.", "/ignorelist", new IgnoreListCommandStrategy())
		);
		commands.put(
				"/away",
				new Command("/away", NORMAL, "Sets your status to away.", "/away <message>", new AwayCommandStrategy(), "message")
		);
		commands.put(
				"/back",
				new Command("/back", NORMAL, "Sets your status to back.", "/back", new BackCommandStrategy())
		);
		commands.put(
				"/who",
				new Command("/who", NORMAL, "Displays information about you.", "/who <channel>", new WhoCommandStrategy(), "channel")
		);
		commands.put(
				"/names",
				new Command("/names", NORMAL, "Displays a list of users in a channel.", "/names <channel>", new NamesCommandStrategy(), "channel")
		);
		commands.put(
				"/list",
				new Command("/list", NORMAL, "Displays a list of channels.", "/list", new ListCommandStrategy())
		);
		commands.put(
				"/time",
				new Command("/time", NORMAL, "Displays the current time.", "/time", new TimeCommandStrategy())
		);
		commands.put(
				"/version",
				new Command("/version", NORMAL, "Displays the version of the server.", "/version", new VersionCommandStrategy())
		);
		commands.put(
				"/ping",
				new Command("/ping", NORMAL, "Pings the server.", "/ping", new PingCommandStrategy())
		);
		commands.put(
				"/quit",
				new Command("/quit", NORMAL, "Disconnects from the server.", "/quit", new QuitCommandStrategy())
		);
		// todo: challenge or not??

		// Fun commands
		commands.put(
				"/dice",
				new Command("/dice", NORMAL, "Rolls a dice (or challenges another user).", "/dice (<user>)", new DiceCommandStrategy(), "user")
		);
		commands.put(
				"/8ball",
				new Command("/8ball", NORMAL, "Asks the magic 8-ball a question.", "/8ball <question>", new EightBallCommandStrategy(), "question")
		);
		commands.put(
				"/flip",
				new Command("/flip", NORMAL, "Flips a coin.", "/flip", new FlipCommandStrategy())
		);
	}

	/**
	 * Returns the {@link Command} with the specified name.
	 *
	 * @param command The name of the command.
	 *
	 * @return The {@link Command} with the specified name.
	 */
	public static Command getCommand(String command) {
		return Optional.ofNullable(commands.getOrDefault(command, null))
		               .orElseThrow(() -> new CommandUnavailableException("Command " + command + " is not available."));
	}

	/**
	 * @return A list of all available commands with its usage.
	 */
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
