import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void newOrder() {
        //new order, buy side
        Order order = messageParser.newOrder("N:1,12.34,100,0");
        assertEquals(1, order.getId());
        assertEquals(12.34, order.getPrice());
        assertEquals(100, order.getQuantity());
        assertTrue(order.isBuySide());

        //new order, sell side
        order = messageParser.newOrder("N:1,12.69,100,1");
        assertEquals(1, order.getId());
        assertEquals(12.69, order.getPrice());
        assertEquals(100, order.getQuantity());
        assertFalse(order.isBuySide());

        invalidNewOrder("N :1,12.69,100,1");
        invalidNewOrder("N:y,12.69,100,1");
        invalidNewOrder("N:1,12.69,100,w");
        invalidNewOrder("Z:1,20,100,3");
    }

    private void invalidNewOrder(String str) {
        try {
            messageParser.newOrder(str);
            fail();

        } catch (Exception e) {
            assertEquals("Illegal message for New Order", e.getMessage());
        }
    }

    @Test
    public void cancelOrder() {
        assertEquals(1, messageParser.cancelId("X:1"));
        assertEquals(69, messageParser.cancelId("X:69"));

        cancelNegative("X:2a0");
        cancelNegative("Y:20");
        cancelNegative("X:-200");
        cancelNegative("X: 20");
    }

    private void cancelNegative(String message) {
        try {
            messageParser.cancelId(message);
            fail();
        } catch (Exception e) {
            assertEquals("Illegal message for cancel order", e.getMessage());
        }
    }

    @Test
    public void amendOrder() {
        Amend amend = messageParser.makeAmend("A:100,0,50");
        assertEquals(amend.getId(), 100);
        assertEquals(amend.QTY_AMEND, amend.getAmendType());
        assertEquals(amend.getQuantity(), 50);
        assertEquals(amend.getPrice(), -1);

        amend = messageParser.makeAmend("A:200,1,100.10");
        assertEquals(amend.getId(), 200);
        assertEquals(amend.PRICE_AMEND, amend.getAmendType());
        assertEquals(amend.getPrice(), 100.10);
        assertEquals(amend.getQuantity(), -1);

        amendOrderNegative("N:100,1,50");
        amendOrderNegative(" A:2.5,0,50");
        amendOrderNegative("A:100,2,50");
        amendOrderNegative("A:,100,0,100");

    }

    private void amendOrderNegative(String message) {
        try {
            messageParser.makeAmend(message);
            fail();
        } catch (Exception e) {
            assertEquals("Illegal message for amend order", e.getMessage());
        }
    }

}
