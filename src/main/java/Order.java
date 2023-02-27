import java.util.Objects;

class Order {
    private int id;
    private double price;
    private int quantity;
    private boolean buySide;

    public Order(int id, double price, int quantity, boolean buySide) {
        validate(id, quantity, price);
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.buySide = buySide;
    }

    private void validate(int id, int quantity, double price) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Qty cannot be less than or equal to zero");

        if (price < 0 || id < 0)
            throw new IllegalArgumentException("Price and id cannot be less than 0." +
                    "\n" + " Zero price represents a Market Order" +
                    "\n" + " Zero id represents orders from other participants");
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isBuySide() {
        return buySide;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getId() == order.getId() &&
                Double.compare(order.getPrice(), getPrice()) == 0 &&
                getQuantity() == order.getQuantity() &&
                isBuySide() == order.isBuySide();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPrice(), getQuantity(), isBuySide());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", buySide=" + buySide +
                '}';
    }
}
