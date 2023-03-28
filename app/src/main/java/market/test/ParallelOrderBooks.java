package market.test;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class ParallelOrderBooks extends OrdersBook {
  private final TreeMap<Integer, Integer> bids = new TreeMap<>(Comparator.reverseOrder());
  private final TreeMap<Integer, Integer> asks = new TreeMap<>();

  private final Phaser bidsPhaser = new Phaser();
  private final Phaser asksPhaser = new Phaser();

  private final ExecutorService bidsExecutor = Executors.newFixedThreadPool(1);
  private final ExecutorService askExecutor = Executors.newFixedThreadPool(1);

  @Override
  protected void querySize(StringBuilder output, int price) {
    asksPhaser.arriveAndAwaitAdvance();
    bidsPhaser.arriveAndAwaitAdvance();
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
    var phaser = bidsOrAsk ? bidsPhaser : asksPhaser;
    var book = bidsOrAsk ? bids : asks;

    phaser.arriveAndAwaitAdvance();
    var e = book.firstEntry();

    output.append(e.getKey()).append(",").append(e.getValue()).append(System.lineSeparator());
  }

  @Override
  protected void update(int price, int size, boolean bidsOrAsk) {
    var phaser = bidsOrAsk ? bidsPhaser : asksPhaser;
    var executor = bidsOrAsk ? bidsExecutor : askExecutor;
    var book = bidsOrAsk ? bids : asks;

    phaser.register();
    executor.submit(() -> {
      if (size == 0) {
        book.remove(price);
      } else {
        book.put(price, size);
      }
      phaser.arriveAndDeregister();
    });
  }

  @Override
  protected void makeOrder(boolean bidsOrAsk, int size) {
    var phaser = bidsOrAsk ? bidsPhaser : asksPhaser;
    var executor = bidsOrAsk ? bidsExecutor : askExecutor;
    var book = bidsOrAsk ? bids : asks;

    phaser.register();
    executor.submit(() -> {
      var remain = size;
      while (remain > 0) {
        var e = book.firstEntry();
        var price = e.getKey();
        int available = e.getValue();
        if (remain >= available) {
          remain -= available;
          book.remove(price);
        } else {
          book.put(price, available - remain);
          remain = 0;
        }
      }
      phaser.arriveAndDeregister();
    });
  }

  @Override
  protected void startup() {
    bidsPhaser.register();
    asksPhaser.register();
  }

  @Override
  protected void shutdown() {
    asksPhaser.arriveAndDeregister();
    bidsPhaser.arriveAndDeregister();
    askExecutor.shutdown();
    bidsExecutor.shutdown();
  }
}
