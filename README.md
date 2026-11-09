# Spring Boot REST Intro

In dieser Übung wird ein einfacher REST-Server in Spring Boot implementiert.

## Zu erwerbende Kompetenzen

1. Sie implementieren einen REST-Controller in Spring Boot schrittweise und können dadurch`@SpringBootApplication`, `@RestController`, `@RequestMapping` und `@GetMapping` ihrer Aufgabe zuweisen.
2. Sie erschrecken das nächste Mal, wenn Sie eine Annotation oder dem Prinzip von Inversion of Control (IoC) begegnen, nicht.
3. Sie prüfen REST-Endpoints mit dem `IntelliJ HTTP Client`, `curl` oder Postman und dokumentieren die Requests in einer `.http`-Datei.
4. Sie repetieren die Elemente einer HTTP-Nachricht (Methode, Pfad, Status-Code, Header und Body) und lesen sie in Request und Response ab.

## Aufgaben

AI soll nur als Erklärbär genutzt werden. Ein lokaler Agent orientiert sich am AGENTS.md, hilft dadurch (hoffentlich) stufengerechter und ist dadurch einem Browser-Chat zu bevorzugen.

| Pfad | Zweck |
| --- | --- |
| `src/main/java` | **Hier schreiben Sie Ihren Code** (z.B. `MovieController`, `UnknownMovieException`). |
| `src/test/java` | **Nur lesen.** Die Tests und Tipps im Javadoc sind die Spezifikation und werden nicht verändert. |
| `sample.http` | Hier schreiben Sie Ihre HTTP-Requests für den IntelliJ HTTP Client. |

1. Erstelle in `src/main/java` einen neuen `MovieController`, als `@RestController`, lies dazu:
   https://www.baeldung.com/spring-controller-vs-restcontroller#spring-mvc-rest-controller
2. Schreibe Schritt für Schritt neue Methoden von Hand, entsprechend den Tests/Tipps in `RestintroApplicationTest` (von oben nach unten). Die Test-Klasse sollte nicht verändert werden und am Schluss sind alle Tests grün. Mach ein Commit nach jedem grünen Test.
3. Schau das `sample.http` file an. IntelliJ besitzt einen built-in HTTP Client ähnlich zu Postman. Schreibe tests zu allen REST Endpoints in diesem .http Format.

## Weiterführendes

- https://www.youtube.com/watch?v=9SGDpanrc8U gibt eine eher lange, aber schonende Einführung in Springboot, geht aber mit JPA/Datenbanken weiter als diese Übung.
