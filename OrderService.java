import java.util.*;
import java.io.*;

public class OrderService {

    private static final String FILE = "orders.txt";

    // ----------------------------------------------------
    // GENERATE UNIQUE ORDER ID
    // ----------------------------------------------------
    public static String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    // ----------------------------------------------------
    // SAVE ORDER  (WITH DELIVERY LOCATION)
    // ----------------------------------------------------
    public static void saveOrder(
            String orderId,
            String userId,
            String role,
            String itemName,
            int qty,
            double total,
            String payMode,
            String deliveryLocation
    ) {

        // INDEXES:
        // 0=orderId,1=userId,2=role,3=item,4=qty,5=total,
        // 6=payMode,7=status,8=delivery,9=date

        String line = orderId + "," + userId + "," + role + "," +
                itemName + "," + qty + "," + total + "," + payMode +
                ",PLACED," + deliveryLocation + "," + new Date();

        FileStorage.appendLine(FILE, line);
    }

    // ----------------------------------------------------
    // READ ALL ORDERS
    // ----------------------------------------------------
    public static List<String> getAllOrders() {
        return FileStorage.readLines(FILE);
    }

    // ----------------------------------------------------
    // UPDATE ORDER STATUS (PREPARING → READY → DELIVERED)
    // ----------------------------------------------------
    public static boolean updateOrderStatus(String orderId, String newStatus) {

        List<String> lines = getAllOrders();
        List<String> updated = new ArrayList<>();

        boolean found = false;

        for (String line : lines) {
            String[] p = line.split(",");

            if (p.length >= 10 && p[0].equals(orderId)) {
                line = p[0] + "," + p[1] + "," + p[2] + "," +
                        p[3] + "," + p[4] + "," + p[5] + "," +
                        p[6] + "," + newStatus + "," + p[8] + "," + p[9];

                found = true;
            }

            updated.add(line);
        }

        if (found) {
            saveUpdated(updated);
        }

        return found;
    }

    // ----------------------------------------------------
    // CANCEL ORDER (ADMIN)
    // WITH WALLET REFUND LOGIC
    // ----------------------------------------------------
    public static boolean cancelOrder(String orderId) {

        List<String> lines = getAllOrders();
        List<String> updated = new ArrayList<>();

        boolean found = false;

        for (String line : lines) {
            String[] p = line.split(",");

            if (p.length >= 10 && p[0].equals(orderId)) {

                String userId = p[1];
                String status = p[7];
                double amount = Double.parseDouble(p[5]);
                String payMode = p[6];

                // Delivered orders cannot be cancelled
                if (status.equals("DELIVERED")) {
                    System.out.println("❌ Cannot cancel a delivered order!");
                    return false;
                }

                // Refund only if paid by wallet
                if (payMode.equals("WALLET")) {
                    WalletService.addMoney(userId, amount);
                    System.out.println("✔ Wallet Refund Processed for: ₹" + amount);
                }

                // Change order status
                line = p[0] + "," + p[1] + "," + p[2] + "," +
                        p[3] + "," + p[4] + "," + p[5] + "," +
                        p[6] + ",CANCELLED," + p[8] + "," + p[9];

                found = true;
            }

            updated.add(line);
        }

        if (found) {
            saveUpdated(updated);
            return true;
        }

        return false;
    }

    // ----------------------------------------------------
    // SHOW ALL ORDERS FOR A PARTICULAR USER
    // ----------------------------------------------------
    public static void showUserOrders(String userId) {

        List<String> lines = getAllOrders();
        boolean found = false;

        for (String l : lines) {
            String[] p = l.split(",");

            if (p.length >= 10 && p[1].equals(userId)) {

                System.out.println(
                        "\nOrder ID : " + p[0] +
                        "\nItem     : " + p[3] +
                        "\nQty      : " + p[4] +
                        "\nTotal    : ₹" + p[5] +
                        "\nPaid Via : " + p[6] +
                        "\nStatus   : " + p[7] +
                        "\nDelivery : " + p[8] +
                        "\nDate     : " + p[9] +
                        "\n------------------------------"
                );

                found = true;
            }
        }

        if (!found) {
            System.out.println("No orders yet.");
        }
    }

    // ----------------------------------------------------
    // PRIVATE: SAVE UPDATED ORDER FILE
    // ----------------------------------------------------
    private static void saveUpdated(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (String s : lines) pw.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
