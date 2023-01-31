package rs.chat.storage.strategies.upload;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.MimeType;
import rs.chat.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UploadMappings {
	record TypeStrategy(MimeType type, FileUploadStrategy strategy) {
	}

	private static final List<TypeStrategy> strategies = new ArrayList<>();

	static {
		strategies.add(new TypeStrategy(MimeType.valueOf("audio/*"), new AudioStrategy()));
		strategies.add(new TypeStrategy(MimeType.valueOf("image/*"), new ImageStrategy()));
		strategies.add(new TypeStrategy(MimeType.valueOf("video/*"), new VideoStrategy()));
		strategies.add(new TypeStrategy(MimeType.valueOf("text/*"), new TextStrategy()));
		strategies.add(new TypeStrategy(MimeType.valueOf("application/pdf"), new PdfStrategy()));
	}

	public static FileUploadStrategy getStrategy(String mimeType) {
		MimeType type = MimeType.valueOf(mimeType);
		return strategies.stream()
		                 .filter(ts -> ts.type().isCompatibleWith(type))
		                 .findFirst()
		                 .orElseThrow(() -> new BadRequestException("Unsupported file type"))
		                 .strategy();
	}
}
