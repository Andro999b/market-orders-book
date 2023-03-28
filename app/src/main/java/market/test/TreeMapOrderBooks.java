package market.test;

import java.util.Comparator;
import java.util.TreeMap;

public class TreeMapOrderBooks extends OrdersBook {
  private final TreeMap<Integer, Integer> bids = new TreeMap<>(Comparator.reverseOrder());
  private final TreeMap<Integer, Integer> asks = new TreeMap<>();

  @Override
  protected void querySize(StringBuilder output, Integer price) {
    var size = asks.get(price);
    if (size != null) {
      output.append(size).append(System.lineSeparator());
      return;
    }

    size = bids.get(price);
    if (size != null) {
      output.append(size).append(System.lineSeparator());
      return;
    }

    output.append("0").append(System.lineSeparator());
  }

  @Override
  protected void queryBest(StringBuilder output, boolean bidsOrAsk) {
    var book = bidsOrAsk ? bids : asks;
    var e = book.firstEntry();

    output.append(e.getKey()).append(",").append(e.getValue()).append(System.lineSeparator());
  }

  @Override
  protected void update(Integer price, Integer size, Boolean bidsOrAsk) {
    var book = bidsOrAsk ? bids : asks;
    if(size == 0) {
      book.remove(price);
    } else {
      book.put(price, size);
    }
  }

  @Override
  protected void makeOrder(boolean bidsOrAsk, Integer size) {
    var book = bidsOrAsk ? bids : asks;

    while (size > 0) {
      var e = book.firstEntry();
      var price = e.getKey();
      int available = e.getValue();
      if (size >= available) {
        size -= available;
        book.remove(price);
      } else {
        book.put(price, available - size);
        size = 0;
      }
    }
  }

}
