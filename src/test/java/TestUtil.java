import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;


public class TestUtil {

    static void assertOrderBookSize(UnifiedOrderBook orderBook, double price, int buyBook, int sellBook) {
        requireNonNull(orderBook.getOrderBook());
        assertEquals(buyBook, orderBook.getOrderBook().get(price).bookSize(true));
        assertEquals(sellBook, orderBook.getOrderBook().get(price).bookSize(false));
    }

    static void printOrderBook(UnifiedOrderBook orderBook) {
        System.out.println("***Order Book***");
        Iterator itr = orderBook.getOrderBook().entrySet().iterator();
        System.out.println("**Buy Book**");
        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry) itr.next();
            System.out.println(pair.getKey());
            OrderQueue queue = (OrderQueue) pair.getValue();
            queue.printBook(true);
        }

        System.out.println("**Sell Book**");
        itr = orderBook.getOrderBook().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry) itr.next();
            System.out.println(pair.getKey());
            OrderQueue queue = (OrderQueue) pair.getValue();
            queue.printBook(false);
        }

        System.out.println("**Look Book**");
        itr = orderBook.getLookBook().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry) itr.next();
            System.out.println(pair.getKey() + " " + pair.getValue());
        }

        int size = orderBook.getMinPrices().size();
        System.out.println("Price MinHeap size: " + size);
        for (int i = 0; i < size; i++) {
            System.out.println(orderBook.getMinPrices().poll() + " ");
        }

        size = orderBook.getMaxPrices().size();
        System.out.println("Price MaxHeap size: " + size);
        for (int i = 0; i < size; i++) {
            System.out.println(orderBook.getMaxPrices().poll() + " ");
        }
    }
}
