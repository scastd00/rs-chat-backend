package rs.chat.router;

import org.springframework.http.HttpMethod;

public record Route(HttpMethod method, String url) {
}
