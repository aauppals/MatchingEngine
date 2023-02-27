import com.google.common.collect.ImmutableSortedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class MessageParser {
    static final String NEW = "N:";
    static final String AMEND = "A:";
    static final String CANCEL = "C:";
    static final String MARKET = "M:";

    private final char COLON = ':';
    private final char BUY = '0';
    private final char SELL = '1';
    private final int MESSAGE_INDEX = 2;
    private final int MARKET_BOOK_DEPTH = 5;


    private final SortedSet<String> PREFIXES;


    MessageParser() {
        PREFIXES = ImmutableSortedSet.of(NEW, AMEND, CANCEL, MARKET);
    }

    String orderType(String message) {
        StringBuilder prefix = new StringBuilder();
        prefix.append(message.charAt(0));
        prefix.append(message.charAt(1));

        if (!PREFIXES.contains(prefix.toString())) {
            throw new IllegalArgumentException("No appropriate prefix found in order");
        }
        return String.valueOf(prefix);
    }

    Order newOrder(String message) {
        if (!message.matches("^N:\\d+,\\d+(\\.\\d+)?,\\d+,[0|1]$")) {
            throw new RuntimeException("Illegal message for New Order");
        }

        String[] orderParams = message.substring(MESSAGE_INDEX).split(",");

        return new Order(Integer.parseInt(orderParams[0]),
                Double.parseDouble(orderParams[1]),
                Integer.parseInt(orderParams[2]),
                sideBuy(orderParams[3]));
    }

    int cancelId(String message) {
        if (!message.matches("^X:\\d+$"))
            throw new RuntimeException("Illegal message for cancel order");
        return Integer.parseInt(message.split(String.valueOf(COLON))[1]);
    }
     /*
        Note: ^,$ are position anchors for regex matching, for start and end respectively.
        It means the whole string will be matched, and not a substring

        \ is escape character;  \d+ matches one or more digits
        ? is optional matching (0 or 1 times)
     */

    Amend makeAmend(String message) {
        if (!message.matches("^A:\\d+,[0|1],\\d+(\\.\\d+)?$"))
            throw new RuntimeException("Illegal message for amend order");

        String[] orderParams = message.substring(MESSAGE_INDEX).split(",");
        int amendType = Integer.parseInt(orderParams[1]);

        if (amendType == Amend.PRICE_AMEND) {
            return new Amend(Integer.parseInt(orderParams[0]), amendType, Double.parseDouble(orderParams[2]));
        } else if (amendType == Amend.QTY_AMEND) {
            return new Amend(Integer.parseInt(orderParams[0]), amendType, Integer.parseInt(orderParams[2]));
        } else throw new IllegalArgumentException("Amend type is not known.");
    }


    List<Order> marketUpdates(String message) {

        if (!message.matches("^M:(\\d+(\\.\\d+)?,\\d+\\|){10}")) {
            throw new RuntimeException("Ill formed message for market update");
        }

        String[] orderStr = message.substring(MESSAGE_INDEX).split("\\|");
        int expectedUpdateSize = 2 * MARKET_BOOK_DEPTH;
        int marketOrderSize = orderStr.length;

        if (marketOrderSize != expectedUpdateSize) {
            throw new RuntimeException("Expected market update size: " + expectedUpdateSize + " but received: " + marketOrderSize);
        }

        int bidIndex = MARKET_BOOK_DEPTH - 1;
        List<Order> orders = new ArrayList<>();

        int i = bidIndex;
        int j = MARKET_BOOK_DEPTH;
        while (i >= 0 && (j <= (MARKET_BOOK_DEPTH * 2) - 1)) {
            String[] properties = orderStr[i].split(",");
            orders.add(new Order(0, Double.parseDouble(properties[0]), Integer.parseInt(properties[1]), true));
            properties = orderStr[j].split(",");
            orders.add(new Order(0, Double.parseDouble(properties[0]), Integer.parseInt(properties[1]), false));
            i--;
            j++;
        }
        return orders;
    }

    private boolean sideBuy(String input) {
        if (input.length() != 1)
            throw new IllegalArgumentException("Order side should be 1 character only");
        if (input.charAt(0) == BUY)
            return true;
        else if (input.charAt(0) == SELL)
            return false;
        else
            throw new IllegalArgumentException("Order side is not supported");
    }
}
