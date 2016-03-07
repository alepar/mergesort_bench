package strings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.SortedSet;
import java.util.TreeSet;

public class MergeStrings {

    public static void main(String[] args) throws Exception {
        final String filenameA = args[0];
        final String filenameB = args[1];
        final String filenameC = args[2];

        final BufferedReader rl = new BufferedReader(new FileReader(filenameA));
        final BufferedReader rr = new BufferedReader(new FileReader(filenameB));
        final BufferedWriter w = new BufferedWriter(new FileWriter(filenameC));

        String sl, sr;
        final long startNanos = System.nanoTime();

        sl = rl.readLine();
        sr = rr.readLine();

        while (sl != null && sr != null) {
            if (sl.compareTo(sr) < 0) {
                w.write(sl);
                sl = rl.readLine();
            } else {
                w.write(sr);
                sr = rr.readLine();
            }
            w.write("\n");
        }

        //todo write tail

        w.close();
        rl.close();
        rr.close();

        final long endNanos = System.nanoTime();

        System.out.println((endNanos-startNanos)/1000_000/100/10.0);
    }
}
