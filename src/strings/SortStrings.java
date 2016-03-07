package strings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.SortedSet;
import java.util.TreeSet;

public class SortStrings {

    public static void main(String[] args) throws Exception {
        final String filename = args[0];

        final BufferedReader r = new BufferedReader(new FileReader(filename));
        final SortedSet<String> strings = new TreeSet<>();

        String line;
        while((line = r.readLine()) != null) {
            strings.add(line);
        }
        r.close();

        final BufferedWriter w = new BufferedWriter(new FileWriter(filename));
        for (String string : strings) {
            w.write(string);
            w.write("\n");
        }
        w.close();
    }
}
