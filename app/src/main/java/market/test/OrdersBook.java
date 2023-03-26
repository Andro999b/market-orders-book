package market.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class OrdersBook {
  public final void process(String inputFile, String outputFile) throws Exception {
    var output = new StringBuilder();

    try (var input = new BufferedReader(new FileReader(inputFile))) {
      input.lines().forEach((line) -> {
        var commandArgs = line.split(",");
        switch (commandArgs[0]) {
          case "u" -> update(
              Integer.parseInt(commandArgs[1]),
              Integer.parseInt(commandArgs[2]),
              commandArgs[3].charAt(0) == 'b');
          case "q" -> {
            if (commandArgs[1].charAt(0) == 'b') {
              queryBest(output, commandArgs[1].charAt(5) == 'b');
            } else {
              querySize(output, Integer.parseInt(commandArgs[2]));
            }
          }
          case "o" -> makeOrder(
              commandArgs[1].charAt(0) == 's',
              Integer.parseInt(commandArgs[2]));
        }
      });
    }

    Files.writeString(Path.of(outputFile), output.toString());
  }

  protected abstract void querySize(StringBuilder output, int price);

  protected abstract void queryBest(StringBuilder output, boolean bidsOrAsk);

  protected abstract void makeOrder(boolean bidsOrAsk, int size);

  protected abstract void update(Integer price, Integer size, Boolean bidsOrAsk);

}
