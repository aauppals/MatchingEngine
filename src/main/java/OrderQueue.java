import java.util.Map;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

class OrderQueue {
    private ConcurrentLinkedQueue<Order> sellQueue;
    private ConcurrentLinkedQueue<Order> buyQueue;

    OrderQueue() {
        sellQueue = new ConcurrentLinkedQueue<>();
        buyQueue = new ConcurrentLinkedQueue<>();
    }

    void fillAndInsert(Order order, Map<Integer, Order> lookBook, PriorityQueue<Double> minPrices, PriorityQueue<Double> maxPrices) {
        boolean result;
        boolean buySide = order.isBuySide();
        result = fill(order, lookBook, buySide ? minPrices : maxPrices); //return the opposite side price as it's fartouch
        if (!result) {
            insert(order, lookBook);

            if (buySide)
                maxPrices.add(order.getPrice());
            else
                minPrices.add(order.getPrice());
        }
    }

    private boolean fill(Order order, Map<Integer,Order> lookBook, PriorityQueue<Double> priorityQueue) {
        return false;
    }

}


