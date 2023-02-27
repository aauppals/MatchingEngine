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
                maxPrices.add(order.getPrice()); //order not filled; price added to same side to update prices in order book
            else
                minPrices.add(order.getPrice());
        }
    }

    private void insert(Order order, Map<Integer, Order> lookBook) {
        getQueueSide(order.isBuySide()).add(order);
        int id = order.getId();

        if (!UnifiedOrderBook.isMarketUpdate(id)) {
            lookBook.put(id, order);
        }
        System.out.printf("Order: %s inserted in the book", order);
    }

    boolean amendQuantity(Amend amend, Order order, Map<Integer, Order> lookBook) {
        Iterator<Order> itr = getQueueSide(order.isBuySide()).iterator(); //iterator on queue
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

    private boolean fill(Order order, Map<Integer, Order> lookBook, PriorityQueue<Double> priorityQueue) {
        boolean result = false;
        boolean partialFill = false;

        Iterator<Order> itr = getQueueSide(!order.isBuySide()).iterator();//return iterator on queue for opposite side
        while (itr.hasNext()) {
            Order restingOrder = itr.next();
            int restingId = restingOrder.getId();
            int orderQty = order.getQuantity();
            int restingQty = restingOrder.getQuantity();
            int diff = orderQty - restingQty;

            if (diff < 0) { // order is filled completely
                order.setQuantity(0);
                int newQuantity = diff * -1;
                restingOrder.setQuantity(newQuantity);

                if (!UnifiedOrderBook.isMarketUpdate(restingId)) {
                    lookBook.get(restingId).setQuantity(newQuantity);
                }
                result = true;

                System.out.println("Order: " + order + " is fully filled.");
                System.out.println("Resting Order: " + restingOrder + " is partially filled. Quantity left is: " + restingOrder.getQuantity());
                break;
            } else if (diff > 0) { //order will be partially filled
                order.setQuantity(diff);
                System.out.println("Resting Order: " + restingOrder + " is fully filled.");
                System.out.println("Order: " + order + " is partially filled. Quantity left is: " + order.getQuantity());

                priorityQueue.remove(restingOrder.getPrice()); //remove price from queue
                itr.remove();//remove underlying Order from queue

                if (!UnifiedOrderBook.isMarketUpdate(restingId)) {
                    lookBook.remove(restingId);
                }
                partialFill = true;

            } else { //order qty is exactly same as resting order qty
                order.setQuantity(0);
                System.out.println("Both resting order: " + restingOrder + " and Order " + order + " are fully filled.");

                priorityQueue.remove(restingOrder.getPrice());
                itr.remove();

                if (!UnifiedOrderBook.isMarketUpdate(restingId))
                    lookBook.remove(restingId);

                result = true;
                break;
            }
        }

        if (!result && !partialFill) {
            System.out.println("Order " + order + " did not get any fills");
        }
        return result;
    }

    void remove(Order order, Map<Integer, Order> lookbook, PriorityQueue<Double> priorityQueue) {
        Iterator<Order> itr = getQueueSide(order.isBuySide()).iterator();
        int id = order.getId();
        while (itr.hasNext()) {
            Order restingOrder = itr.next();
            double price = restingOrder.getPrice();
            if (restingOrder.getId() == id) {
                System.out.println(restingOrder + " cancelled.");
                itr.remove();
                lookbook.remove(id);
                priorityQueue.remove(price);
                break;
            }
        }
    }

    private ConcurrentLinkedQueue<Order> getQueueSide(boolean buySide) {
        if (buySide) return buyQueue;
        return sellQueue;
    }

}


