package ch.bbw.m320.restintro;

import java.util.Objects;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Diese Klasse soll nicht modifiziert werden! Dieser Test simuliert die Client-Seite mittels WebTestClient (<a
 * href="https://spring.getdocs.org/en-US/spring-framework-docs/docs/testing/integration-testing/webtestclient.html">Beispiele</a>).
 * Implementiere die Server-Seite (ein RestController), sodass alle Tests grün werden.
 */
@AutoConfigureWebTestClient
@SpringBootTest
class RestintroApplicationTest implements WithAssertions {

	private static final MovieDto AVENGERS = new MovieDto(0, "Avengers", 2012);

	@Autowired
	private WebTestClient webClient;

	@BeforeEach
	void showRequestDetailsInLog() {
		webClient = webClient.mutate().codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true)).build();
	}

	/**
	 * Einfache Ping-Pong-Methode, nutze die Annotation @GetMapping und gib einen einfachen String zurück
	 * https://www.baeldung.com/spring-controller-vs-restcontroller#spring-mvc-rest-controller
	 */
	@Test
	void pingPong() {
		// Auch ohne die vollständige Syntax von webClient zu verstehen können wir hier Folgendes herauslesen:
		// - die HTTP-Methode ist ein GET (https://de.wikipedia.org/wiki/Hypertext_Transfer_Protocol#HTTP_GET)
		// - der Pfad ist /api/ping
		// - Die HTTP-Response hat z.B. ein Status-Code 200 OK
		// - Im Body der HTTP-Response steht der einfache String "pong"
		webClient.get().uri("/api/ping").exchange().expectStatus().is2xxSuccessful() // üblicherweise HTTP/200 OK
			.expectBody(String.class).isEqualTo("pong");
	}

	@Test
	void pingPongHasContentTypeText() {
		// Der HTTP-Header mit Namen "Content-Type" kann explizit gesetzt werden.
		// Die Syntax der Annotation ändert sich dadurch:
		// @GetMapping("ping") ist dasselbe wie @GetMapping(value="ping") und wir wollen noch mehr mit
		// @GetMapping(value = "ping", produces = ...)
		webClient.get()
			.uri("/api/ping")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectHeader()
			.contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
			.expectBody(String.class)
			.isEqualTo("pong");
	}

	/**
	 * Nun geben wir ein (hartcodiertes) {@link MovieDto} zurück und nutzen {@link PathVariable}.
	 */
	@Test
	void getMovieById() {
		// für den Anfang genügt es, den Film hartzucodieren
		webClient.get()
			.uri("/api/movies/{id}", 0)
			.exchange()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON) // (das ist der default, bei @RestController)
			.expectBody(MovieDto.class)
			.isEqualTo(AVENGERS);
	}

	/**
	 * Das funktioniert auch mit Listen.
	 */
	@Test
	void getMoviesReturnsAList() {
		// für den Anfang genügt eine hartcodierte Liste mit einem Film
		webClient.get().uri("/api/movies").exchange().expectStatus().is2xxSuccessful().expectBodyList(MovieDto.class).contains(AVENGERS);
	}

	/**
	 * Nun speichern wir mit einem POST und holen uns denselben Record wieder mit einem GET. Beachte, dass diese POST-Methode gleich wieder
	 * ein {@link MovieDto} zurückgibt mit gesetzter ID. https://www.appsdeveloperblog.com/postmapping-requestbody-spring-mvc/
	 */
	@Test
	void saveAndGet() {
		// wir geben keine ID an, die setzt die Server-Seite für uns
		var movieToCreate = new MovieDto(null, "The Dark Knight", 2008);
		var createdMovie = Objects.requireNonNull(webClient.post()  // POST, um einen neuen Film zu erstellen
			.uri("/api/movies")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(movieToCreate)
			.exchange()
			.expectBody(MovieDto.class)
			.returnResult()
			.getResponseBody());
		assertThat(createdMovie.title()).isEqualTo(movieToCreate.title());
		assertThat(createdMovie.id()).as("expected the server to assign a new ID").isPositive().isNotEqualTo(AVENGERS.id());
		// nun lesen wir ihn wieder
		webClient.get().uri("/api/movies/{id}", createdMovie.id()).exchange().expectBody(MovieDto.class).isEqualTo(createdMovie);
	}

	/**
	 * Nun starten wir mit eigenem Fehlerhandling. Erstelle dazu eine neue `UnknownMovieException`:
	 * https://www.baeldung.com/exception-handling-for-rest-with-spring#3-responsestatusexceptionresolver (beachte `@ResponseStatus`)
	 */
	@Test
	void getMovieByUnknownId() {
		webClient.get().uri("/api/movies/{id}", 1337) // diese ID ist unbekannt
			.exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST); // wir erwarten hier ein HTTP/400 (nicht 404)
	}

	/**
	 * Falls wir die Response weiter anpassen wollen, können wir stattdessen eine {@link org.springframework.http.ResponseEntity}
	 * zurückgeben. https://www.baeldung.com/spring-response-entity
	 */
	@Test
	void customFuuMethod() {
		webClient.patch()  // PATCH Methode
			.uri("/api/fuu").exchange().expectStatus().isEqualTo(212) // eigener HTTP-Status 212
			.expectHeader().valueEquals("X-Men", "Wolverine") // eigener Header
			.expectBody(String.class).isEqualTo("bar");
	}

	/**
	 * Eine DELETE-Methode gibt im Normalfall keinen Body zurück, wir wollen in diesem Fall ein HTTP/204. → `ResponseEntity<Void>`
	 */
	@Test
	void createAndDelete() {
		var createdMovie = Objects.requireNonNull(webClient.post()  // POST, um einen neuen Film zu erstellen
			.uri("/api/movies")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(new MovieDto(null, "Whatever", 1997))
			.exchange()
			.expectBody(MovieDto.class)
			.returnResult()
			.getResponseBody());
		webClient.delete().uri("/api/movies/" + createdMovie.id()).exchange().expectStatus().isNoContent();  // ist ein HTTP/204
	}

	@Test
	void deleteUnknown() {
		webClient.delete().uri("/api/movies/1337")  // ID existiert nicht
			.exchange().expectStatus().isBadRequest();
	}
}
