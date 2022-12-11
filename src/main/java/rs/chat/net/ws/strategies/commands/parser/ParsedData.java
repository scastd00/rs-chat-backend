package rs.chat.net.ws.strategies.commands.parser;

import java.util.List;

public record ParsedData(String data, List<String> params, boolean isCommand) {
}
