package rs.chat.net.ws.strategies.commands.parser;

import rs.chat.net.ws.strategies.commands.Params;

public record ParsedData(String data, Params params, Type type) {
	public enum Type {
		MESSAGE,
		COMMAND,
		MENTION
	}

	public boolean isMessage() {
		return type == Type.MESSAGE;
	}
}
