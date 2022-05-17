public interface OrderBook {
    boolean amend(Amend amend);

    boolean cancel(int id);

    boolean InsertOrder(Order order);
}
