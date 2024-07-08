/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.cashcard.model;

import java.io.IOException;
import org.assertj.core.api.Assertions;
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

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = new CashCard(99L, 123.45);

        Assertions.assertThat(jsonTester.write(cashCard)).isStrictlyEqualToJson("expected.json");
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
                            "amount":123.45
                          }
                          """;

        Assertions.assertThat(jsonTester.parse(expected)).isEqualTo(new CashCard(99L, 123.45));
        Assertions.assertThat(jsonTester.parseObject(expected).id()).isEqualTo(99);
        Assertions.assertThat(jsonTester.parseObject(expected).amount()).isEqualTo(123.45);
    }

}
