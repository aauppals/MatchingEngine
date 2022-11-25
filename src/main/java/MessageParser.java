import com.google.common.collect.ImmutableSortedSet;

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
        if (!message.matches("^N:\\d+,\\d+(\\.\\d+)?,\\d+,[0|1]]$")) {
            throw new RuntimeException("Illegal message for New Order");
        }

        String[] orderParams = message.substring(MESSAGE_INDEX).split(",");

        return new Order(Integer.parseInt(orderParams[0]),
                Double.parseDouble(orderParams[1]),
                Integer.parseInt(orderParams[2]),
                sideBuy(orderParams[3]));
    }

     /*
        Note: ^,$ are position anchors for regex matching, for start and end respectively.
        It means the whole string will be matched, and not a substring

        \ is escape character;  \d+ matches one or more digits
        ? is optional matching (0 or 1 times)
     */

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
