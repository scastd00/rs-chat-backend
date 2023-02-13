package rs.chat.net.ws.strategies.commands;

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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static rs.chat.net.ws.strategies.commands.Command.CommandType.ADMIN;
import static rs.chat.net.ws.strategies.commands.Command.CommandType.NORMAL;
import static rs.chat.net.ws.strategies.commands.Command.CommandType.TEACHER;

/**
 * Record that represents a command that can be executed by a client.
 *
 * @param command      The command to be executed.
 * @param type         The type of command.
 * @param description  A description of the command.
 * @param usage        The usage of the command.
 * @param strategy     The strategy to be used to execute the command.
 * @param sendToOthers Whether the command should be sent to other clients.
 * @param paramNames   The names of the parameters that the command takes, if any.
 */
public record Command(
		String command,
		CommandType type,
		String description,
		String usage,
		CommandStrategy strategy,
		boolean sendToOthers,
		String... paramNames) {

	public static final Command $HELP = new Command("/help", NORMAL, "Displays a list of all available commands.", "/help", new HelpCommandStrategy(), false);
	public static final Command $CLEAR = new Command("/clear", NORMAL, "Clears the chat window.", "/clear", new ClearCommandStrategy(), false);
	public static final Command $ME = new Command("/me", NORMAL, "Sends an action message.", "/me <message>", new MeCommandStrategy(), true, "message");
	public static final Command $NICK = new Command("/nick", NORMAL, "Changes your nickname.", "/nick <nickname>", new NickCommandStrategy(), false, "nickname");
	public static final Command $JOIN = new Command("/join", NORMAL, "Joins a channel.", "/join <channel>", new JoinCommandStrategy(), false, "channel");
	public static final Command $LEAVE = new Command("/leave", NORMAL, "Leaves a channel.", "/leave <channel>", new LeaveCommandStrategy(), false, "channel");
	public static final Command $MSG = new Command("/msg", NORMAL, "Sends a private message to a user.", "/msg <user> <message>", new MsgCommandStrategy(), true, "user", "message");
	public static final Command $WHOIS = new Command("/whois", NORMAL, "Displays information about a user.", "/whois <user>", new WhoIsCommandStrategy(), false, "user");
	public static final Command $INVITE = new Command("/invite", NORMAL, "Invites a user to a channel.", "/invite <user> <channel>", new InviteCommandStrategy(), true, "user", "channel");
	public static final Command $KICK = new Command("/kick", TEACHER, "Kicks a user from a channel.", "/kick <user> <channel>", new KickCommandStrategy(), true, "user", "channel");
	public static final Command $BAN = new Command("/ban", TEACHER, "Bans a user from a channel.", "/ban <user> <channel>", new BanCommandStrategy(), true, "user", "channel");
	public static final Command $UNBAN = new Command("/unban", TEACHER, "Unbans a user from a channel.", "/unban <user> <channel>", new UnbanCommandStrategy(), true, "user", "channel");
	public static final Command $TOPIC = new Command("/topic", TEACHER, "Changes the topic of a channel.", "/topic <channel> <topic>", new TopicCommandStrategy(), true, "channel", "topic");
	public static final Command $MODE = new Command("/mode", TEACHER, "Changes the mode of a channel.", "/mode <channel> <mode>", new ModeCommandStrategy(), true, "channel", "mode");
	public static final Command $OP = new Command("/op", ADMIN, "Gives a user operator status in a channel.", "/op <user> <channel>", new OpCommandStrategy(), true, "user", "channel");
	public static final Command $DEOP = new Command("/deop", ADMIN, "Removes a user's operator status in a channel.", "/deop <user> <channel>", new DeOpCommandStrategy(), true, "user", "channel");
	public static final Command $IGNORE = new Command("/ignore", NORMAL, "Ignores a user.", "/ignore <user>", new IgnoreCommandStrategy(), true, "user");
	public static final Command $UNIGNORE = new Command("/unignore", NORMAL, "Removes the ignore to a user.", "/unignore <user>", new UnIgnoreCommandStrategy(), true, "user");
	public static final Command $IGNORELIST = new Command("/ignorelist", NORMAL, "Displays a list of ignored users.", "/ignorelist", new IgnoreListCommandStrategy(), true);
	public static final Command $AWAY = new Command("/away", NORMAL, "Sets your status to away.", "/away", new AwayCommandStrategy(), false);
	public static final Command $BACK = new Command("/back", NORMAL, "Sets your status to back.", "/back", new BackCommandStrategy(), false);
	public static final Command $WHO = new Command("/who", NORMAL, "Displays information about you.", "/who <channel>", new WhoCommandStrategy(), true, "channel");
	public static final Command $NAMES = new Command("/names", NORMAL, "Displays a list of users in a channel.", "/names <channel>", new NamesCommandStrategy(), true, "channel");
	public static final Command $LIST = new Command("/list", NORMAL, "Displays a list of channels.", "/list", new ListCommandStrategy(), true);
	public static final Command $TIME = new Command("/time", NORMAL, "Displays the current time.", "/time", new TimeCommandStrategy(), true);
	public static final Command $VERSION = new Command("/version", NORMAL, "Displays the version of the server.", "/version", new VersionCommandStrategy(), false);
	public static final Command $PING = new Command("/ping", NORMAL, "Pings the server.", "/ping", new PingCommandStrategy(), false);
	public static final Command $QUIT = new Command("/quit", NORMAL, "Disconnects from the server.", "/quit", new QuitCommandStrategy(), true);

	// Fun commands
	// todo: challenge or not??
	public static final Command $DICE = new Command("/dice", NORMAL, "Rolls a dice (or challenges another user).", "/dice (<user>)", new DiceCommandStrategy(), true, "user");
	public static final Command $8_BALL = new Command("/8ball", NORMAL, "Asks the magic 8-ball a question.", "/8ball <question>", new EightBallCommandStrategy(), true, "question");
	public static final Command $FLIP = new Command("/flip", NORMAL, "Flips a coin.", "/flip", new FlipCommandStrategy(), true);

	public static final List<Command> ALL_COMMANDS = Arrays.asList(
			$HELP, $CLEAR, $ME, $NICK, $JOIN, $LEAVE, $MSG, $WHOIS, $INVITE, $KICK, $BAN, $UNBAN,
			$TOPIC, $MODE, $OP, $DEOP, $IGNORE, $UNIGNORE, $IGNORELIST, $AWAY, $BACK, $WHO, $NAMES, $LIST,
			$TIME, $VERSION, $PING, $QUIT, $DICE, $8_BALL, $FLIP
	);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Command command1 = (Command) o;
		return Objects.equals(command, command1.command) &&
				type == command1.type &&
				Objects.equals(description, command1.description) &&
				Objects.equals(usage, command1.usage) &&
				Objects.equals(strategy, command1.strategy) &&
				Arrays.equals(paramNames, command1.paramNames);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(command, type, description, usage, strategy);
		result = 31 * result + Arrays.hashCode(paramNames);
		return result;
	}

	@Override
	public String toString() {
		return "Command{" +
				"command='" + command + '\'' +
				", type=" + type +
				", description='" + description + '\'' +
				", usage='" + usage + '\'' +
				", strategy=" + strategy +
				", paramNames=" + Arrays.toString(paramNames) +
				'}';
	}

	/**
	 * Type of the command.
	 * <p>
	 * The type of the command determines how the command is executed.
	 * <ul>
	 *     <li>{@link CommandType#NORMAL} - The command is executed by all clients.</li>
	 *     <li>{@link CommandType#TEACHER} - The command is executed by the client that sent the command if the client is a teacher.</li>
	 *     <li>{@link CommandType#ADMIN} - The command is executed by the client that sent the command if the client is an admin.</li>
	 * </ul>
	 */
	public enum CommandType {
		NORMAL,
		TEACHER,
		ADMIN
	}
}
