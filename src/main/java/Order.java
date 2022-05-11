import java.util.Objects;

public class Order {
    private int id;
    private double price;
    private int quantity;
    private boolean buy;

    public Order(int id, double price, int quantity, boolean buy) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.buy = buy;
    }

    public int getId() {return id;}

    public double getPrice() {return price;}

    public int getQuantity() {return quantity;}

    public boolean isBuy() {return buy;}

    public void setPrice(double price) {this.price = price;}

    public void setQuantity(int quantity) {this.quantity = quantity;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getId() == order.getId() &&
                Double.compare(order.getPrice(), getPrice()) == 0 &&
                getQuantity() == order.getQuantity() &&
                isBuy() == order.isBuy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPrice(), getQuantity(), isBuy());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", sideTrade=" + buy +
                '}';
    }
}
