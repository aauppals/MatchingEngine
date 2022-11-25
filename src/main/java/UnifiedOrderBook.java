class UnifiedOrderBook implements OrderBook {
//Does not keep track of market updates

    private static final int MARKET_UPDATE_ID = 0;

    @Override
    public boolean amend(Amend amend) {
        return false;
    }

    @Override
    public boolean cancel(int id) {
        return false;
    }

    @Override
    public boolean fillAndInsert(Order order) {
        return false;
    }

    public static boolean isMarketUpdate(int id) {
        if (id == MARKET_UPDATE_ID) return true;
        else return false;
    }

}
