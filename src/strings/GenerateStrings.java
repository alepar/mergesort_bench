package strings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class GenerateStrings {

    public static void main(String[] args) throws Exception {
        final String filename = args[0];

        final BufferedWriter w = new BufferedWriter(new FileWriter(filename));
        final Random rng = new Random();

        final int bound = 'z' - 'a' + 1;
        final int cols = 128;
        final int rows = 1024*1024*1024 / cols;

        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                w.write((char)(rng.nextInt(bound) + 'a'));
            }
            w.write("\n");
        }

        w.close();
    }
}
