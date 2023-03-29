package market.test;

public class App {

    public static void main(String[] args) throws Exception {
        // var ordersBook = new PriorityQueueOrderBooks();
        // var ordersBook = new TreeMapOrderBooks();
        // var ordersBook = new ParallelOrderBooks();
        // var ordersBook = new TreeSetOrderBooks();
        var ordersBook = new CustomDataStructureOrderBooks();
        ordersBook.process("input.txt", "output.txt");
    }
}
