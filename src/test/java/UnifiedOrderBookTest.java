import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnifiedOrderBookTest {
    private UnifiedOrderBook orderBook;

    @BeforeEach
    public void setup() {
        orderBook = new UnifiedOrderBook();
    }

    @AfterEach
    public void print() {
        TestUtil.printOrderBook(orderBook);
        orderBook.clear();
    }

    @Test
    public void FillAndInsert() {
        Order order1 = new Order(1, 10.0, 100, true);
        Order order2 = new Order(2, 10.1, 100, false);
        assertTrue(orderBook.fillAndInsert(order1));
        assertTrue(orderBook.fillAndInsert(order2));
        TestUtil.assertOrderBookSize(orderBook, 10.0, 1, 0);
        TestUtil.assertOrderBookSize(orderBook, 10.1, 0, 1);
        TestUtil.assertOtherSizes(orderBook, 1, 1, 2);
        Order existing = new Order(2, 10.1, 100, false);
        assertFalse(orderBook.fillAndInsert(existing));
        TestUtil.assertOrderBookSize(orderBook, 10.0, 1, 0);
        TestUtil.assertOrderBookSize(orderBook, 10.1, 0, 1);
        TestUtil.assertOtherSizes(orderBook, 1, 1, 2);
        Order order3 = new Order(3, 10.1, 100, true);
        assertTrue(orderBook.fillAndInsert(order3));
        TestUtil.assertOrderBookSize(orderBook, 10.0, 1, 0);
        TestUtil.assertOrderBookSize(orderBook, 10.1, 0, 0);
        TestUtil.assertOtherSizes(orderBook, 0, 1, 1);
        Order order4 = new Order(4, 10.1, 100, false);
        assertTrue(orderBook.fillAndInsert(order4));
        TestUtil.assertOrderBookSize(orderBook, 10.0, 1, 0);
        TestUtil.assertOrderBookSize(orderBook, 10.1, 0, 1);
        TestUtil.assertOtherSizes(orderBook, 1, 1, 2);
        assertTrue(orderBook.cancel(4));
        TestUtil.assertOrderBookSize(orderBook, 10.0, 1, 0);
        TestUtil.assertOrderBookSize(orderBook, 10.1, 0, 0);
        TestUtil.assertOtherSizes(orderBook, 0, 1, 1);
    }

}

