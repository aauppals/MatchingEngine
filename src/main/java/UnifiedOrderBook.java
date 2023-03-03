import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class UnifiedOrderBook implements OrderBook {
//Does not keep track of market updates

    private static final int MARKET_UPDATE_ID = 0;

    private final Map<Integer, Order> lookBook;
    private final Map<Double, OrderQueue> orderBook;
    private PriorityQueue<Double> minPrices;
    private PriorityQueue<Double> maxPrices;
    private ReentrantLock lock;

    public UnifiedOrderBook() {
        lookBook = new ConcurrentHashMap<>();
        orderBook = new ConcurrentHashMap<>();
        minPrices = new PriorityQueue<>();
        maxPrices = new PriorityQueue<>((o1, o2) -> -Double.compare(o1, o2)); //compare returns -1 if o1<o2
        lock = new ReentrantLock(true);
    }

    @Override
    public boolean fillAndInsert(Order order) {
        lock.lock();
        try {
            int id = order.getId();
            if (id != MARKET_UPDATE_ID && getOrder(id) != null) {
                System.out.println("fillAndInsert failed as an order with same ID already exists. ID: " + id);
                return false;
            }

            if (order.getPrice() == 0 && !setMarketPrice(order)) {
                return false;
            }

            OrderQueue queues = orderBook.get(order.getPrice());
            if (queues != null) {
                queues.fillAndInsert(order, lookBook, maxPrices, minPrices);
            } else {
                queues = new OrderQueue();
                queues.fillAndInsert(order, lookBook, maxPrices, minPrices);
                orderBook.put(order.getPrice(), queues);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private boolean setMarketPrice(Order order) {
        lock.lock();
        try {
            System.out.println("Market Order: " + order + " will be given a price of far-touch, " +
                    "and any remaining quantity will be inserted in the queue at that price.");
            if (order.isBuySide() && !minPrices.isEmpty()) {
                order.setPrice(minPrices.peek());
                return true;
            } else if (!order.isBuySide() && !maxPrices.isEmpty()) {
                order.setPrice(maxPrices.peek());
                return true;
            }
            System.out.println("No limit order to match against market order. Order: " + order +
                    " This order will be ignored.");
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean amend(Amend amend) {
        lock.lock();
        try {
            int id = amend.getId();
            Order order = getOrder(id);

            if (order == null) {
                System.out.println("Order with id " + id + " not found, amend failed.");
                return false;
            }

            if (amend.getAmendType() == Amend.PRICE_AMEND && amend.getPrice() != order.getPrice()) {
                cancel(id);
                order.setPrice(amend.getPrice());
                return fillAndInsert(order);
            } else if (amend.getAmendType() == Amend.QTY_AMEND && amend.getQuantity() != order.getQuantity()) {
                return orderBook.get(order.getPrice()).amendQuantity(amend, order, lookBook);
            }
            return false;

        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean cancel(int id) {
        lock.lock();
        if (getOrder(id) == null) return false;

        try {
            Order order = getOrder(id);
            if (order == null) {
                System.out.println("Order with id: " + id + " does not exist in lookbook, cancellation  failed.");
                return false;
            }
            OrderQueue queues = orderBook.get(order.getPrice());

            if (queues != null) {
                if (order.isBuySide()) {
                    queues.remove(order, lookBook, maxPrices);
                } else {
                    queues.remove(order, lookBook, minPrices);
                }
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private Order getOrder(int id) {
        if (id == MARKET_UPDATE_ID) return null;
        return lookBook.get(id);
    }

    public static boolean isMarketUpdate(int id) {
        if (id == MARKET_UPDATE_ID) return true;
        else return false;
    }

    public Map<Integer, Order> getLookBook() {
        return lookBook;
    }

    public Map<Double, OrderQueue> getOrderBook() {
        return orderBook;
    }

    public PriorityQueue<Double> getMinPrices() {
        return minPrices;
    }

    public PriorityQueue<Double> getMaxPrices() {
        return maxPrices;
    }
}
