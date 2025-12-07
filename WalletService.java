import java.util.*;

public class WalletService {

    private static final String FILE = "wallets.txt";

    private static Map<String, Double> loadAll() {
        List<String> lines = FileStorage.readLines(FILE);
        Map<String, Double> map = new HashMap<>();
        for (String l : lines) {
            String[] p = l.split(",");
            if (p.length == 2) {
                try {
                    map.put(p[0], Double.parseDouble(p[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    private static void saveAll(Map<String, Double> map) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            lines.add(e.getKey() + "," + e.getValue());
        }
        FileStorage.writeLines(FILE, lines);
    }

    public static double getBalance(String userId) {
        return loadAll().getOrDefault(userId, 0.0);
    }

    public static double addMoney(String userId, double amount) {
        Map<String, Double> map = loadAll();
        double bal = map.getOrDefault(userId, 0.0) + amount;
        map.put(userId, bal);
        saveAll(map);
        return bal;
    }

    public static boolean deduct(String userId, double amount) {
        Map<String, Double> map = loadAll();
        double bal = map.getOrDefault(userId, 0.0);
        if (bal < amount) return false;
        map.put(userId, bal - amount);
        saveAll(map);
        return true;
    }
}
