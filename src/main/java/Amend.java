import java.util.Objects;

public class Amend {
    private final int id;
    private final int amendType;
    private final int quantity;
    private final double price;

    private final int QTY_AMEND = 1;
    private final int PRICE_AMEND = 1;

    public Amend(int id, int amendType, int quantity) {
        if (quantity >= 0 && amendType == QTY_AMEND) {
            this.id = id;
            this.amendType = amendType;
            this.quantity = quantity;
            this.price = -1;
        } else
            throw new IllegalArgumentException("Quantity cannot be <= 0; AmendType for quantity should be QTY_AMEND");
    }

    public Amend(int id, int amendType, double price) {
        if (price >= 0 && amendType == PRICE_AMEND) {
            this.id = id;
            this.amendType = amendType;
            this.price = price;
            this.quantity = -1;
        } else
            throw new IllegalArgumentException("Price cannot be <= 0; AmendType for price should be PRICE_AMEND");
    }

    public int getId() {return id;}

    public int getAmendType() {return amendType;}

    public int getQuantity() {return quantity;}

    public double getPrice() {return price;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amend)) return false;
        Amend amend = (Amend) o;
        return getId() == amend.getId() && getAmendType() == amend.getAmendType() && getQuantity() == amend.getQuantity() && Double.compare(amend.getPrice(), getPrice()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmendType(), getQuantity(), getPrice());
    }
}
