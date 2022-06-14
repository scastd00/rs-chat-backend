package ule.chat.router;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public final class Route<P extends String> {
	private HttpMethod method;
	private P url;
}
