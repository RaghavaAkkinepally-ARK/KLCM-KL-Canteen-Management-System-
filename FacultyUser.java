import java.util.*;

public class FacultyUser extends BaseUser implements Payable {

    public FacultyUser(String empCode) {
        super(empCode, empCode, "FACULTY");
    }

    @Override
    public void showPortal(Scanner sc) {

        System.out.println(ConsoleColor.CYAN + "\n--- FACULTY PORTAL ---" + ConsoleColor.RESET);

        int ch;

        do {
            System.out.println("\n1) View Menu");
            System.out.println("2) Order Food");
            System.out.println("3) Wallet");
            System.out.println("4) Payment History");
            System.out.println("5) Track My Orders");
            System.out.println("0) Logout");
            System.out.print("Enter: ");

            ch = readInt(sc);

            switch (ch) {
                case 1 -> MenuService.printMenu();
                case 2 -> orderFood(sc);
                case 3 -> wallet(sc);
                case 4 -> history();
                case 5 -> OrderService.showUserOrders(id);
                case 0 -> System.out.println("Logout...");
                default -> System.out.println("Invalid!");
            }

        } while (ch != 0);
    }

    // =====================================================================
    // ORDER FOOD
    // =====================================================================
    @Override
    public void orderFood(Scanner sc) {

        MenuService.printMenu();

        System.out.print("Enter Item ID: ");
        String idItem = sc.nextLine().trim();

        MenuItem item = MenuService.findById(idItem);

        if (item == null) {
            System.out.println("Invalid Item!");
            return;
        }

        System.out.print("Quantity: ");
        int qty = readInt(sc);

        // Stock check
        if (!MenuService.hasStock(idItem, qty)) {
            System.out.println(ConsoleColor.RED + "Not enough stock available!" + ConsoleColor.RESET);
            return;
        }

        double total = qty * item.price;
        double bal = WalletService.getBalance(id);

        System.out.println("Wallet Balance: ₹" + bal);
        System.out.println("Total Amount: ₹" + total);

        // -------------------------------
        // Choose Payment Method
        // -------------------------------
        System.out.println("\nSelect Payment Mode:");
        System.out.println("1) Wallet");
        System.out.println("2) UPI QR");
        System.out.print("Enter: ");
        int p = readInt(sc);

        boolean paid = false;
        String mode = "";

        if (p == 1) {
            if (!WalletService.deduct(id, total)) {
                System.out.println("Not enough wallet balance!");
                return;
            }
            paid = true;
            mode = "WALLET";
        }

        else if (p == 2) {
            String upi = "upi://pay?pa=klcanteen@upi&pn=KL+Canteen&am="
                    + total + "&cu=INR&tn=Faculty+Order";

            paid = QRCodeGenerator.showQRAndSimulatePayment(upi, total, id);
            mode = "UPI_QR";
        }

        else {
            System.out.println("Invalid Payment Mode!");
            return;
        }

        if (!paid) {
            System.out.println("Payment failed!");
            return;
        }

        // Deduct stock
        MenuService.reduceStock(idItem, qty);

        // Delivery option
        System.out.print("Enter Delivery Cabin No. (or 0 for self-pickup): ");
        String cabin = sc.nextLine().trim();
        if (cabin.equals("")) cabin = "0";

        // Save order
        String orderId = OrderService.generateOrderId();

        OrderService.saveOrder(orderId, id, "FACULTY",
                item.name, qty, total, mode, cabin);

        // Log payment
        FileStorage.appendLine("payments.txt",
                "FACULTY," + id + "," + item.name + "," +
                        qty + "," + total + "," + mode + "," + new Date());

        int est = 7 + (qty * 2);

        System.out.println(ConsoleColor.GREEN +
                "Order Placed Successfully!" + ConsoleColor.RESET);

        System.out.println(ConsoleColor.YELLOW +
                "Order ID: " + orderId +
                "\nDelivery: " + (cabin.equals("0") ? "Self Pickup" : "Cabin " + cabin) +
                "\nEstimated Time: " + est + " minutes" +
                ConsoleColor.RESET);
    }

    // =====================================================================
    // WALLET
    // =====================================================================
    private void wallet(Scanner sc) {
        double bal = WalletService.getBalance(id);
        System.out.println("Your Balance: ₹" + bal);

        System.out.print("Enter Amount to Add: ");
        double amt;

        try {
            amt = Double.parseDouble(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid Amount!");
            return;
        }

        if (amt <= 0) return;

        WalletService.addMoney(id, amt);
        System.out.println("Updated Balance: ₹" + WalletService.getBalance(id));
    }

    // =====================================================================
    // PAYMENT HISTORY
    // =====================================================================
    private void history() {
        List<String> lines = FileStorage.readLines("payments.txt");
        boolean found = false;

        System.out.println("\n--- PAYMENT HISTORY ---");

        for (String s : lines) {
            String[] p = s.split(",");

            if (p.length >= 6 && p[0].equals("FACULTY") && p[1].equals(id)) {
                System.out.println("Item: " + p[2] +
                        " | Qty: " + p[3] +
                        " | Amount: ₹" + p[4] +
                        " | Mode: " + p[5] +
                        " | Date: " + p[6]);
                found = true;
            }
        }

        if (!found)
            System.out.println("No Payments Yet!");
    }
}
