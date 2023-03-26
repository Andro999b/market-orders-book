package market.test;

public class App {

    public static void main(String[] args) throws Exception {
        // var ordersBook = new PriorityQueueOrderBooks();
        var ordersBook = new TreeMapOrderBooks();
        // var ordersBook = new TreeSetOrderBooks();
        ordersBook.process("input.txt", "output.txt");
    }
}
