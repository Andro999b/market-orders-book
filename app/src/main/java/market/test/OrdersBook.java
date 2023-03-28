package market.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class OrdersBook {
  public final void process(String inputFile, String outputFile) throws Exception {
    // startup();
    var output = new StringBuilder();

    try (var input = Files.newBufferedReader(Path.of(inputFile))) {
      int ch;
      while((ch = input.read()) != -1) {
        switch(ch) {
          case 'u' -> {
            input.skip(1); // skip comma
            update(readInt(input), readInt(input), input.read() == 'b');
            skipToNewLine(input);
          }
          case 'q' -> {
            input.skip(1);
            if(input.read() == 'b') {
              input.skip(4);
              queryBest(output, input.read() == 'b');
              skipToNewLine(input);
            } else {
              input.skip(4);
              querySize(output, readInt(input));
            }
          }
          case 'o' -> {
            input.skip(1);
            var bidsOrAsk = input.read() == 's';
            input.skip(bidsOrAsk ? 4 : 3);
            makeOrder(bidsOrAsk, readInt(input));
          }
        }
      }
      // input.lines().forEach((line) -> {
      //   switch (line.charAt(0)) {
      //     case 'u' -> {
      //       var commandArgs = line.split(",");
      //       update(
      //         Integer.parseInt(commandArgs[1]),
      //         Integer.parseInt(commandArgs[2]),
      //         commandArgs[3].charAt(0) == 'b');
      //     }
      //     case 'q' -> {
      //       if (line.charAt(2) == 'b') {
      //         queryBest(output, line.charAt(7) == 'b');
      //       } else {
      //         querySize(output, Integer.parseInt(line.substring(line.lastIndexOf(',') + 1)));
      //       }
      //     }
      //     case 'o' -> makeOrder(
      //         line.charAt(2) == 's',
      //         Integer.parseInt(line.substring(line.lastIndexOf(',') + 1)));
      //   }
      // });
    } finally {
      // shutdown();
    }

    Files.writeString(Path.of(outputFile), output.toString());
  }

  private int readInt(BufferedReader input) throws IOException {
    int acc = 0;
    int ch;
    while(true) {
      ch = input.read() - 48;
      if(ch < 0 || ch > 9) {
        break;
      }
      acc = ch + acc * 10;
    }
    return acc;
  }

  private void skipToNewLine(BufferedReader input) throws IOException {
    int ch;
    while(true) {
      ch = input.read();
      if(ch == -1 || ch == '\n') {
        return;
      }
    }
  }

  protected abstract void querySize(StringBuilder output, int price);

  protected abstract void queryBest(StringBuilder output, boolean bidsOrAsk);

  protected abstract void makeOrder(boolean bidsOrAsk, int size);

  protected abstract void update(int price, int size, boolean bidsOrAsk);

  protected void shutdown() {}
  protected void startup() {}
}
