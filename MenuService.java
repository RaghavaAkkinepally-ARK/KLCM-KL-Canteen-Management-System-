import java.util.*;
import java.io.*;

public class MenuService {

    public static List<MenuItem> items = new ArrayList<>();

    static {
        loadMenu();
    }

    // -----------------------------------------------------
    // LOAD MENU FROM FILE
    // -----------------------------------------------------
    public static void loadMenu() {
        items.clear();
        List<String> lines = FileStorage.readLines("menu.txt");

        for (String l : lines) {
            String[] p = l.split(",");
            try {
                String id = p[0];
                String name = p[1];
                double price = Double.parseDouble(p[2]);
                int stock = Integer.parseInt(p[3]);
                int threshold = Integer.parseInt(p[4]);

                items.add(new MenuItem(id, name, price, stock, threshold));
            } catch (Exception e) {
                // Skip invalid line
            }
        }
    }

    // -----------------------------------------------------
    // SAVE MENU TO FILE
    // -----------------------------------------------------
    public static void saveMenu() {
        List<String> lines = new ArrayList<>();
        for (MenuItem m : items) {
            lines.add(m.id + "," + m.name + "," + m.price + "," + m.stock + "," + m.threshold);
        }
        FileStorage.writeLines("menu.txt", lines);
    }

    // -----------------------------------------------------
    // PRINT MENU
    // -----------------------------------------------------
    public static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("ID\tItem\tPrice\tStock");

        for (MenuItem m : items) {
            System.out.println(m.id + "\t" + m.name + "\t" + m.price + "\t" + m.stock);
        }
    }

    // -----------------------------------------------------
    // FIND ITEM BY ID
    // -----------------------------------------------------
    public static MenuItem findById(String id) {
        for (MenuItem m : items) {
            if (m.id.equals(id)) return m;
        }
        return null;
    }

    // -----------------------------------------------------
    // ADD ITEM
    // -----------------------------------------------------
    public static void addItem(String id, String name, double price, int stock, int threshold) {
        items.add(new MenuItem(id, name, price, stock, threshold));
        saveMenu();
    }

    // -----------------------------------------------------
    // UPDATE PRICE
    // -----------------------------------------------------
    public static boolean updatePrice(String id, double newPrice) {
        MenuItem m = findById(id);
        if (m == null) return false;

        m.price = newPrice;
        saveMenu();
        return true;
    }

    // -----------------------------------------------------
    // DELETE ITEM
    // -----------------------------------------------------
    public static boolean deleteItem(String id) {
        MenuItem m = findById(id);
        if (m == null) return false;

        items.remove(m);
        saveMenu();
        return true;
    }

    // -----------------------------------------------------
    // REDUCE STOCK AFTER ORDER
    // -----------------------------------------------------
    public static boolean reduceStock(String id, int qty) {
        MenuItem m = findById(id);
        if (m == null) return false;

        if (m.stock < qty) return false;

        m.stock -= qty;
        saveMenu();
        return true;
    }

    // -----------------------------------------------------
    // UPDATE STOCK (ADMIN)
    // -----------------------------------------------------
    public static void updateStock(String id, int newStock, int newThreshold) {
        MenuItem m = findById(id);
        if (m != null) {
            m.stock = newStock;
            m.threshold = newThreshold;
            saveMenu();
        }
    }

    // -----------------------------------------------------
    // HAS STOCK?  (Fixes FacultyUser error)
    // -----------------------------------------------------
    public static boolean hasStock(String id, int qty) {
        MenuItem m = findById(id);
        return (m != null && m.stock >= qty);
    }

    // -----------------------------------------------------
    // LOW STOCK ITEMS
    // -----------------------------------------------------
    public static List<MenuItem> getLowStockItems() {
        List<MenuItem> low = new ArrayList<>();

        for (MenuItem m : items) {
            if (m.stock <= m.threshold) low.add(m);
        }
        return low;
    }

    // -----------------------------------------------------
    // SORT MENU BY PRICE (Bubble Sort)
    // -----------------------------------------------------
    public static void printMenuSortedByPrice() {
        List<MenuItem> sorted = new ArrayList<>(items);

        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = 0; j < sorted.size() - i - 1; j++) {
                if (sorted.get(j).price > sorted.get(j + 1).price) {
                    Collections.swap(sorted, j, j + 1);
                }
            }
        }

        System.out.println("\n--- SORTED MENU BY PRICE ---");
        for (MenuItem m : sorted) {
            System.out.println(m.id + "\t" + m.name + "\t" + m.price);
        }
    }

    // -----------------------------------------------------
    // SEARCH BY NAME
    // -----------------------------------------------------
    public static int searchItemByName(String text) {
        text = text.toLowerCase();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name.toLowerCase().contains(text)) {
                return i;
            }
        }
        return -1;
    }
}

