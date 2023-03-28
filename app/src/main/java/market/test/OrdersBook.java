package market.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class OrdersBook {
  public final void process(String inputFile, String outputFile) throws Exception {
    startup();
    var output = new StringBuilder();

    try (var input = new BufferedReader(new FileReader(inputFile))) {
      input.lines().forEach((line) -> {
        switch (line.charAt(0)) {
          case 'u' -> {
            var commandArgs = line.split(",");
            update(
              Integer.parseInt(commandArgs[1]),
              Integer.parseInt(commandArgs[2]),
              commandArgs[3].charAt(0) == 'b');
          }
          case 'q' -> {
            if (line.charAt(2) == 'b') {
              queryBest(output, line.charAt(7) == 'b');
            } else {
              querySize(output, Integer.parseInt(line.substring(line.lastIndexOf(',') + 1)));
            }
          }
          case 'o' -> makeOrder(
              line.charAt(2) == 's',
              Integer.parseInt(line.substring(line.lastIndexOf(',') + 1)));
        }
      });
    } finally {
      shutdown();
    }

    Files.writeString(Path.of(outputFile), output.toString());
  }

  protected abstract void querySize(StringBuilder output, Integer price);

  protected abstract void queryBest(StringBuilder output, boolean bidsOrAsk);

  protected abstract void makeOrder(boolean bidsOrAsk, Integer size);

  protected abstract void update(Integer price, Integer size, Boolean bidsOrAsk);

  protected void shutdown() {}
  protected void startup() {}
}
