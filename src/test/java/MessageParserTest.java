import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageParserTest {

    private MessageParser messageParser;

    @BeforeEach
    public void setup() {
        messageParser = new MessageParser();
    }

    @Test
    public void orderType() {
        assertEquals(messageParser.orderType("N:"), MessageParser.NEW);
        assertEquals(messageParser.orderType("A:"), MessageParser.AMEND);
        assertEquals(messageParser.orderType("C:"), MessageParser.CANCEL);
        assertEquals(messageParser.orderType("M:"), MessageParser.MARKET);

        try {
            messageParser.orderType("Z:");
            fail();
        } catch (Exception e) {
            assertEquals("No appropriate prefix found in order", e.getMessage());
        }
    }

}
