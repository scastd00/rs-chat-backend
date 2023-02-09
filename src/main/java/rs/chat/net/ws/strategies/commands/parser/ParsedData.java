package rs.chat.net.ws.strategies.commands.parser;

public record ParsedData(String data, Params params, Type type) {
	public enum Type {
		TEXT,
		COMMAND,
		MENTION
	}

	public boolean isText() {
		return type == Type.TEXT;
	}
}
