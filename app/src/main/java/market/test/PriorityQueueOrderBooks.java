package market.test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityQueueOrderBooks extends OrdersBook {
  private final Map<Integer, Integer> bids = new HashMap<>(1000000);
  private final Queue<Integer> bidsOrder = new PriorityQueue<>(1000000, Comparator.reverseOrder());
  private final Map<Integer, Integer> asks = new HashMap<>(1000000);
  private final Queue<Integer> asksOrder = new PriorityQueue<>(1000000);

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
    var order = bidsOrAsk ? bidsOrder : asksOrder;

    var price = order.peek();
    var size = book.get(price);

    output.append(price).append(",").append(size).append(System.lineSeparator());
  }

  @Override
  protected void makeOrder(boolean bidsOrAsk, Integer size) {
    var book = bidsOrAsk ? bids : asks;
    var order = bidsOrAsk ? bidsOrder : asksOrder;

    while (size > 0) {
      var price = order.peek();
      int available = book.get(price);
      if (size >= available) {
        size -= available;
        book.remove(price);
        order.poll();
      } else {
        book.put(price, available - size);
        size = 0;
      }
    }
  }

  @Override
  protected void update(Integer price, Integer size, Boolean bidsOrAsk) {
    var book = bidsOrAsk ? bids : asks;
    var order = bidsOrAsk ? bidsOrder : asksOrder;

    if (size > 0) {
      if (book.put(price, size) == null) {
        order.offer(price);
      }
    } else {
      if (book.remove(price) != null) {
        order.remove(price);
      }
    }
  }
}
