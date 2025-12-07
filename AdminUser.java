import java.util.*;
import java.util.stream.*;

public class AdminUser extends BaseUser {

    public AdminUser(String mail) {
        super(mail, mail, "ADMIN");
    }

    @Override
    public void showPortal(Scanner sc) {

        System.out.println(ConsoleColor.CYAN + "\n--- ADMIN DASHBOARD ---" + ConsoleColor.RESET);

        int ch;
        do {
            System.out.println("\n1) View Menu");
            System.out.println("2) Add Menu Item");
            System.out.println("3) Update Item Price");
            System.out.println("4) Delete Menu Item");
            System.out.println("5) Update Stock");
            System.out.println("6) Low Stock Alerts");
            System.out.println("7) View All Orders");
            System.out.println("8) Update Order Status");
            System.out.println("9) Cancel Order");
            System.out.println("10) Daily Sales Summary");
            System.out.println("11) Most Ordered Items");
            System.out.println("0) Logout");
            System.out.print("Enter: ");

            ch = readInt(sc);

            switch (ch) {
                case 1 -> MenuService.printMenu();
                case 2 -> addMenuItem(sc);
                case 3 -> updatePrice(sc);
                case 4 -> deleteItem(sc);
                case 5 -> updateStock(sc);
                case 6 -> showLowStock();
                case 7 -> viewAllOrders();
                case 8 -> updateOrderStatus(sc);
                case 9 -> cancelOrder(sc);
                case 10 -> dailySales();
                case 11 -> mostOrdered();
                case 0 -> System.out.println("Logging out admin...");
                default -> System.out.println("Invalid option!");
            }

        } while (ch != 0);
    }

    // --------------------------------------------------------------------
    // ADD ITEM
    // --------------------------------------------------------------------
    private void addMenuItem(Scanner sc) {

        System.out.print("Enter Item ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Price: ");
        double price = Double.parseDouble(sc.nextLine());

        System.out.print("Enter Stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Threshold: ");
        int threshold = Integer.parseInt(sc.nextLine());

        MenuService.addItem(id, name, price, stock, threshold);

        System.out.println(ConsoleColor.GREEN + "Item Added!" + ConsoleColor.RESET);
    }

    // --------------------------------------------------------------------
    // UPDATE PRICE
    // --------------------------------------------------------------------
    private void updatePrice(Scanner sc) {

        System.out.print("Enter Item ID: ");
        String id = sc.nextLine();

        System.out.print("Enter New Price: ");
        double price = Double.parseDouble(sc.nextLine());

        if (MenuService.updatePrice(id, price))
            System.out.println("Price Updated!");
        else
            System.out.println("Item Not Found!");
    }

    // --------------------------------------------------------------------
    // DELETE ITEM
    // --------------------------------------------------------------------
    private void deleteItem(Scanner sc) {
        System.out.print("Enter Item ID: ");
        String id = sc.nextLine();

        if (MenuService.deleteItem(id))
            System.out.println("Item Deleted!");
        else
            System.out.println("Item Not Found!");
    }

    // --------------------------------------------------------------------
    // UPDATE STOCK
    // --------------------------------------------------------------------
    private void updateStock(Scanner sc) {

        System.out.print("Enter Item ID: ");
        String id = sc.nextLine();

        MenuItem item = MenuService.findById(id);

        if (item == null) {
            System.out.println("Item Not Found!");
            return;
        }

        System.out.println("Current Stock: " + item.stock);
        System.out.println("Current Threshold: " + item.threshold);

        System.out.print("Enter New Stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        System.out.print("Enter New Threshold: ");
        int threshold = Integer.parseInt(sc.nextLine());

        MenuService.updateStock(id, stock, threshold);
        System.out.println("Stock Updated!");
    }

    // --------------------------------------------------------------------
    // LOW STOCK ALERTS
    // --------------------------------------------------------------------
    private void showLowStock() {
        List<MenuItem> list = MenuService.getLowStockItems();

        if (list.isEmpty()) {
            System.out.println("All items have sufficient stock.");
            return;
        }

        System.out.println("\n⚠ LOW STOCK ITEMS ⚠");

        for (MenuItem m : list) {
            System.out.println(m.id + " - " + m.name +
                    " | Stock: " + m.stock +
                    " | Threshold: " + m.threshold);
        }
    }

    // --------------------------------------------------------------------
    // VIEW ALL ORDERS
    // --------------------------------------------------------------------
    private void viewAllOrders() {
        List<String> orders = OrderService.getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("\n----- ALL ORDERS -----");

        for (String l : orders) {
            String[] p = l.split(",");
            System.out.println(
                "OrderID: " + p[0] +
                " | User: " + p[1] +
                " | Role: " + p[2] +
                " | Item: " + p[3] +
                " | Qty: " + p[4] +
                " | Amount: ₹" + p[5] +
                " | Payment: " + p[6] +
                " | Status: " + p[7] +
                " | Delivery: " + p[8] +
                " | Date: " + p[9]
            );
        }
    }

    // --------------------------------------------------------------------
    // UPDATE ORDER STATUS
    // --------------------------------------------------------------------
    private void updateOrderStatus(Scanner sc) {

        System.out.print("Enter Order ID: ");
        String orderId = sc.nextLine();

        System.out.println("1) PREPARING");
        System.out.println("2) READY");
        System.out.println("3) DELIVERED");
        System.out.print("Choose: ");

        int c = readInt(sc);

        String newStatus = switch (c) {
            case 1 -> "PREPARING";
            case 2 -> "READY";
            case 3 -> "DELIVERED";
            default -> null;
        };

        if (newStatus == null) {
            System.out.println("Invalid!");
            return;
        }

        if (OrderService.updateOrderStatus(orderId, newStatus))
            System.out.println("Order Updated!");
        else
            System.out.println("Order Not Found!");
    }

    // --------------------------------------------------------------------
    // CANCEL ORDER
    // --------------------------------------------------------------------
    private void cancelOrder(Scanner sc) {

        System.out.print("Enter Order ID to Cancel: ");
        String orderId = sc.nextLine();

        if (OrderService.cancelOrder(orderId))
            System.out.println(ConsoleColor.RED + "Order Cancelled!" + ConsoleColor.RESET);
        else
            System.out.println("Order Not Found!");
    }

    // --------------------------------------------------------------------
    // DAILY SALES SUMMARY
    // --------------------------------------------------------------------
    private void dailySales() {

        List<String> orders = OrderService.getAllOrders();

        double total = 0;

        for (String s : orders) {
            String[] p = s.split(",");
            total += Double.parseDouble(p[5]);
        }

        System.out.println("\nToday's Total Sales: ₹" + total);
    }

    // --------------------------------------------------------------------
    // MOST ORDERED ITEMS
    // --------------------------------------------------------------------
    private void mostOrdered() {

        List<String> orders = OrderService.getAllOrders();

        Map<String, Integer> countMap = new HashMap<>();

        for (String s : orders) {
            String[] p = s.split(",");
            String item = p[3];

            countMap.put(item, countMap.getOrDefault(item, 0) + 1);
        }

        System.out.println("\n--- Most Ordered Items ---");

        countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e ->
                        System.out.println(e.getKey() + " => " + e.getValue() + " orders"));
    }
}
