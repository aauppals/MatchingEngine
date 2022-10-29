class Amend {
    private final int amendType;
    private final int id;
    private final int quantity;
    private final double price;

    private final int QTY_AMEND = 0;
    private final int PRICE_AMEND = 1;

    Amend(int id, int amendType, int quantity) {
        if (id >= 0 && quantity >= 0 && amendType == QTY_AMEND) {
            this.id = id;
            this.amendType = amendType;
            this.quantity = quantity;
            this.price = -1;
        } else
            throw new IllegalArgumentException("Quantity and ID cannot be <= 0; AmendType for quantity should be QTY_AMEND");
    }

    Amend(int id, int amendType, double price) {
        if (id >= 0 && price >= 0 && amendType == PRICE_AMEND) {
            this.id = id;
            this.amendType = amendType;
            this.price = price;
            this.quantity = -1;
        } else
            throw new IllegalArgumentException("Price or id cannot be <=0; Amendtype for price should be price_amend");
    }

    public int getId() {
        return id;
    }

    public int getAmendType() {
        return amendType;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

}
