import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileStorage {

    public static void appendLine(String fileName, String line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(line);
        } catch (IOException e) {
            System.out.println("Error writing to " + fileName + ": " + e.getMessage());
        }
    }

    public static List<String> readLines(String fileName) {
        try {
            Path p = Paths.get(fileName);
            if (!Files.exists(p)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(p);
        } catch (IOException e) {
            System.out.println("Error reading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void writeLines(String fileName, List<String> lines) {
        try {
            Files.write(Paths.get(fileName), lines);
        } catch (IOException e) {
            System.out.println("Error writing " + fileName + ": " + e.getMessage());
        }
    }
}
