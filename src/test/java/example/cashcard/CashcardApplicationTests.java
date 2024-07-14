package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import example.cashcard.model.CashCard;
import java.net.URI;
import net.minidev.json.JSONArray;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/99", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");

        Assertions.assertThat(id).isNotNull();
        Assertions.assertThat(id).isEqualTo(99);

        Double amount = context.read("$.amount");

        Assertions.assertThat(amount).isNotNull();
        Assertions.assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "BAD-CREDENTIALS")
                .getForEntity("/cashcards/99", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("BAD-USER", "abc123")
                .getForEntity("/cashcards/99", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/1000", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard cashCard = new CashCard(null, 250.00, null);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .postForEntity("/cashcards", cashCard, Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI uriCreated = response.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity(uriCreated, String.class);
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        Double amount = context.read("$.amount");

        Assertions.assertThat(id).isNotNull();
        Assertions.assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int cashCardCount = documentContext.read("$.length()");
        Assertions.assertThat(cashCardCount).isEqualTo(3);

        JSONArray jsonArrayIds = documentContext.read("$..id");
        Assertions.assertThat(jsonArrayIds).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray jsonArrayAmounts = documentContext.read("$..amount");
        Assertions.assertThat(jsonArrayAmounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards?page=1&size=1", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        Assertions.assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray array = documentContext.read("$[*]");
        Assertions.assertThat(array.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].amount");
        Assertions.assertThat(amount).isEqualTo(150.00);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray array = documentContext.read("$[*]");
        Assertions.assertThat(array.size()).isEqualTo(3);

        JSONArray arrayAmounts = documentContext.read("$..amount");
        Assertions.assertThat(arrayAmounts).containsExactly(1.00, 123.45, 150.00);
    }

    @Test
    void shouldRejectUsersWhoAreNotCardOwners() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("hank-owns-no-cards", "qrs456")
                .getForEntity("/cashcards/99", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/102", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCashCard() {
        CashCard cashCard = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(cashCard);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/99", HttpMethod.PUT, httpEntity, Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> responseGet = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/99", String.class);

        Assertions.assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(responseGet.getBody());

        Number id = context.read("$.id");
        Double amount = context.read("$.amount");

        Assertions.assertThat(id).isEqualTo(99);
        Assertions.assertThat(amount).isEqualTo(19.99);
    }

    @Test
    void shouldNotUpdateACashCardThatDoesNotExist() {
        CashCard cashCard = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(cashCard);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/999999", HttpMethod.PUT, httpEntity, Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
        CashCard kumarsCard = new CashCard(null, 333.33, null);
        HttpEntity<CashCard> request = new HttpEntity<>(kumarsCard);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/102", HttpMethod.PUT, request, Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard() {
        ResponseEntity<Void> responseDelete = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/99", HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/99", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteACashCardThatDoesNotExist() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/99999", HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        ResponseEntity<Void> responseDelete = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .exchange("/cashcards/102", HttpMethod.GET, HttpEntity.EMPTY, Void.class);

        Assertions.assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("kumar2", "xyz789")
                .getForEntity("/cashcards/102", String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void contextLoads() {
    }

}
