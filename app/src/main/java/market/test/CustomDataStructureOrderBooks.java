package market.test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CustomDataStructureOrderBooks extends OrdersBook {

  private static class OrdersList {
    interface PriceAndSize {
      Integer getPrice();

      Integer getSize();
    }

    static class Node implements PriceAndSize {
      Integer price;
      Integer size;
      Node next;
      Node prev;

      @Override
      public Integer getPrice() {
        return price;
      }

      @Override
      public Integer getSize() {
        return size;
      }
    }

    Node head;

    final Map<Integer, Node> nodesByPrice = new HashMap<Integer, Node>();
    final Comparator<Integer> comparator;

    OrdersList(boolean reverse) {
      comparator = reverse ? Comparator.reverseOrder() : Comparator.naturalOrder();
    }

    void put(Integer price, Integer size) {
      var node = nodesByPrice.get(price);

      if (node != null) {
        node.size = size;
      } else {
        var newNode = new Node();
        newNode.price = price;
        newNode.size = size;

        if (head == null) {
          head = newNode;
        } else if (comparator.compare(head.price, price) >= 0) {
          newNode.next = head;
          newNode.next.prev = newNode;
          head = newNode;
        } else {
          Node current = head;

          while (current.next != null &&
              comparator.compare(current.next.price, price) < 0) {
            current = current.next;
          }

          newNode.next = current.next;

          if (current.next != null)
            newNode.next.prev = newNode;

          current.next = newNode;
          newNode.prev = current;
        }

        nodesByPrice.put(price, newNode);
      }
    }

    PriceAndSize peek() {
      if (head == null)
        return null;

      return head;
    }

    Integer get(Integer price) {
      var node = nodesByPrice.get(price);

      if (node == null)
        return null;

      return node.size;
    }

    void remove(Integer price) {
      var node = nodesByPrice.remove(price);

      if (node != null) {
        if(node == head) {
          head = node.next;
          if(head != null) head.prev = null;
          return;
        }

        if (node.next != null) {
          node.next.prev = node.prev;
        }

        if (node.prev != null) {
          node.prev.next = node.next;
        }
      }
    }

    PriceAndSize poll() {
      var node = head;
      
      head = node.next;
      if(head != null) head.prev = null;

      if(node != null) {
        nodesByPrice.remove(node.price);
      }
      
      return head;
    }
  }

  private final OrdersList bids = new OrdersList(true);
  private final OrdersList asks = new OrdersList(false);

  @Override
  protected void querySize(StringBuilder output, int price) {
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
    var e = book.peek();

    output.append(e.getPrice()).append(",").append(e.getSize()).append(System.lineSeparator());
  }

  @Override
  protected void makeOrder(boolean bidsOrAsk, int size) {
    var book = bidsOrAsk ? bids : asks;

    while (size > 0) {
      var e = book.peek();
      var price = e.getPrice();
      int available = e.getSize();
      if (size >= available) {
        size -= available;
        book.poll();
      } else {
        book.put(price, available - size);
        size = 0;
      }
    }
  }

  @Override
  protected void update(int price, int size, boolean bidsOrAsk) {
    var book = bidsOrAsk ? bids : asks;
    if(size == 0) {
      book.remove(price);
    } else {
      book.put(price, size);
    }
  }

}
