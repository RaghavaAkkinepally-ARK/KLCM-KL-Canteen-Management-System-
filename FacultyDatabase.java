import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FacultyDatabase {

    private static final String FILE = "faculty.txt";

    static {
        try { new File(FILE).createNewFile(); } catch(Exception e){}
    }

    public static void saveFaculty(String empCode, String name, String mail, String password) {
        FileStorage.appendLine(FILE, empCode+","+name+","+mail+","+password);
    }

    public static List<String[]> getAllFaculty() {
        List<String[]> out = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE));
            for (String l : lines) out.add(l.split(","));
        } catch(Exception e){}
        return out;
    }
}
