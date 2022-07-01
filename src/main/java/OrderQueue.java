import java.util.Map;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

class OrderQueue {
    private final ConcurrentLinkedQueue<Order> sellQueue;
    private final ConcurrentLinkedQueue<Order> buyQueue;

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

    boolean amendQuantity(Amend amend, Order order, Map<Integer, Order> lookBook) {
        Iterator<Order> itr = getQueueSide(order.isBuySide()).iterator();
        while (itr.hasNext()) {
            Order restingOrder = itr.next();
            if (restingOrder.getId() == amend.getId()) {
                if (amend.getQuantity() < restingOrder.getQuantity()) {
                    restingOrder.setQuantity(amend.getQuantity());
                    System.out.println(restingOrder + "quantity down-amended");
                    lookBook.replace(restingOrder.getId(), restingOrder);
                } else {
                    Order newOrder = new Order(order.getId(), order.getPrice(), amend.getQuantity(), order.isBuySide());
                    itr.remove();//remove from Queue
                    lookBook.remove(amend.getId());
                    //Queue priority is lost with quantity-up amend
                    insert(newOrder, lookBook);
                }
                break;
            }
        }
        return true;
    }


    private void insert(Order order, Map<Integer, Order> lookBook) {
        getQueueSide(order.isBuySide()).add(order);
        int id = order.getId();

        if (!UnifiedOrderBook.isMarketUpdate(id)) {
            lookBook.put(id, order);
        }
        System.out.printf("Order: %s inserted in the book", order);
    }

    private ConcurrentLinkedQueue<Order> getQueueSide(boolean buySide) {
        if (buySide) return buyQueue;
        return sellQueue;
    }


    private boolean fill(Order order, Map<Integer, Order> lookBook, PriorityQueue<Double> priorityQueue) {
        return false;
    }

}


