/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.model;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

/**
 *
 * @author DGIE - J72 Aprovisionamiento de Tecnología y Datos Económicos
 */
@JsonTest
public class CashcardJsonTest {

    @Autowired
    private JacksonTester<CashCard> jsonTester;

    @Autowired
    private JacksonTester<CashCard[]> jsonListTester;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "sarah1"),
                new CashCard(100L, 1.00, "sarah1"),
                new CashCard(101L, 150.00, "sarah1"));
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = cashCards[0];

        Assertions.assertThat(jsonTester.write(cashCard)).isStrictlyEqualToJson("single.json");
        Assertions.assertThat(jsonTester.write(cashCard)).hasJsonPathNumberValue("@.id");
        Assertions.assertThat(jsonTester.write(cashCard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);

        Assertions.assertThat(jsonTester.write(cashCard)).hasJsonPathNumberValue("@.amount");
        Assertions.assertThat(jsonTester.write(cashCard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                          {
                            "id":99,
                            "amount":123.45, 
                            "owner": "sarah1"
                          }
                          """;

        Assertions.assertThat(jsonTester.parse(expected)).isEqualTo(new CashCard(99L, 123.45, "sarah1"));
        Assertions.assertThat(jsonTester.parseObject(expected).id()).isEqualTo(99);
        Assertions.assertThat(jsonTester.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    void cashCardListSerializationTest() throws IOException {
        Assertions.assertThat(jsonListTester.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected = """
                          [
                            { "id": 99, "amount": 123.45, "owner": "sarah1"},
                            { "id": 100, "amount": 1.00, "owner": "sarah1"},
                            { "id": 101, "amount": 150.00, "owner": "sarah1"}
                          ]
                          """;

        Assertions.assertThat(jsonListTester.parse(expected)).isEqualTo(cashCards);
    }

}
