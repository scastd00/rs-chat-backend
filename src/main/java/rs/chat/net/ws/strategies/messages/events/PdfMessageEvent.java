package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.PDF_MESSAGE;

public class PdfMessageEvent extends MessageEvent {
	public PdfMessageEvent(Object source, String username) {
		super(source, PDF_MESSAGE.type(), username);
	}
}
