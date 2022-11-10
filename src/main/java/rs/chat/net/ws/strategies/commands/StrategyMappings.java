package rs.chat.net.ws.strategies.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.CommandUnavailableException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class that maps commands to their respective strategies.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrategyMappings {
	private static final Map<String, CommandStrategy> strategies = new HashMap<>();

	static {
		strategies.put("/help", new HelpCommandStrategy());
		strategies.put("/clear", new ClearCommandStrategy());
		strategies.put("/me", new MeCommandStrategy());
		strategies.put("/nick", new NickCommandStrategy());
		strategies.put("/join", new JoinCommandStrategy());
		strategies.put("/part", new PartCommandStrategy());
		strategies.put("/msg", new MsgCommandStrategy());
		strategies.put("/whois", new WhoIsCommandStrategy());
		strategies.put("/invite", new InviteCommandStrategy());
		strategies.put("/kick", new KickCommandStrategy());
		strategies.put("/ban", new BanCommandStrategy());
		strategies.put("/unban", new UnbanCommandStrategy());
		strategies.put("/topic", new TopicCommandStrategy());
		strategies.put("/mode", new ModeCommandStrategy());
		strategies.put("/op", new OpCommandStrategy());
		strategies.put("/deop", new DeOpCommandStrategy());
		strategies.put("/ignore", new IgnoreCommandStrategy());
		strategies.put("/unignore", new UnIgnoreCommandStrategy());
		strategies.put("/ignorelist", new IgnoreListCommandStrategy());
		strategies.put("/away", new AwayCommandStrategy());
		strategies.put("/back", new BackCommandStrategy());
		strategies.put("/who", new WhoCommandStrategy());
		strategies.put("/names", new NamesCommandStrategy());
		strategies.put("/list", new ListCommandStrategy());
		strategies.put("/time", new TimeCommandStrategy());
		strategies.put("/version", new VersionCommandStrategy());
		strategies.put("/ping", new PingCommandStrategy());
		strategies.put("/quit", new QuitCommandStrategy());
	}

	public static CommandStrategy decideStrategy(String command) {
		return Optional.ofNullable(strategies.get(command))
		               .orElseThrow(() -> new CommandUnavailableException("Command " + command + " is not available."));
	}

	public static String getAvailableCommandsWithDescriptionAndUsage() {
		StringBuilder sb = new StringBuilder("##");

		strategies.forEach((command, strategy) -> sb.append(command)
		                                            .append(" - ")
		                                            .append(strategy.getDescriptionOfCommand())
		                                            .append(" Usage: ")
		                                            .append(strategy.getUsageOfCommand())
		                                            .append("##")
		);

		return sb.replace(sb.length() - 2, sb.length(), "").toString();
	}
}
