package cat.udl.eps.softarch.fll.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MediaContentValidationTest {

	@Test
	void validConstruction() {
		assertDoesNotThrow(() -> MediaContent.create("https://example.com/video.mp4", "video"));
	}

	@Nested
	class NullId {

		@Test
		void nullUrlThrows() {
			assertThrows(DomainValidationException.class,
					() -> MediaContent.create(null, "video"));
		}

		@Test
		void blankUrlThrows() {
			assertThrows(DomainValidationException.class,
					() -> MediaContent.create("", "video"));
		}
	}

	@Nested
	class EmptyName {

		@Test
		void nullTypeThrows() {
			assertThrows(DomainValidationException.class,
					() -> MediaContent.create("https://example.com/video.mp4", null));
		}

		@Test
		void blankTypeThrows() {
			assertThrows(DomainValidationException.class,
					() -> MediaContent.create("https://example.com/video.mp4", ""));
		}
	}
}
