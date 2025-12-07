import java.util.*;

public class StudentUser extends BaseUser implements Payable {

    public StudentUser(String regId) {
        super(regId, regId, "STUDENT");
    }

    // ---------------------------
    // BRANCH IDENTIFICATION
    // ---------------------------
    private String getBranch(String regId) {

        if (regId.length() < 5) return "INVALID";

        String mid = regId.substring(2, 5);   // digits 3–5

        return switch (mid) {
            case "200" -> "Bachupally";
            case "100" -> "Vijayawada";
            case "000" -> "Aziznagar";
            default -> "INVALID";
        };
    }

    @Override
    public void showPortal(Scanner sc) {
        logLogin();

        // Validate Branch First
        String campus = getBranch(id);
        if (campus.equals("INVALID")) {
            System.out.println(ConsoleColor.RED +
                    "\n❌ Invalid Branch Code in Reg ID!" +
                    "\nAccess Denied.\n" +
                    ConsoleColor.RESET);
            return;  // **Stop login**
        }

        System.out.println(ConsoleColor.CYAN + "\n--- STUDENT PORTAL ---" + ConsoleColor.RESET);
        System.out.println(ConsoleColor.GREEN +
                "Welcome " + id + " @ " + campus + " Campus" +
                ConsoleColor.RESET);

        int choice;
        do {
            System.out.println("\n1) View Menu");
            System.out.println("2) Order Food");
            System.out.println("3) Wallet");
            System.out.println("4) Payment History");
            System.out.println("5) Track My Orders");
            System.out.println("0) Logout");
            System.out.print("Enter: ");

            choice = readInt(sc);

            switch (choice) {
                case 1 -> MenuService.printMenu();
                case 2 -> orderFood(sc);
                case 3 -> handleWallet(sc);
                case 4 -> showHistory();
                case 5 -> OrderService.showUserOrders(id);
                case 0 -> System.out.println("Logging out student...");
                default -> System.out.println("Invalid!");
            }

        } while (choice != 0);
    }

    // ------------------------------------
    // ORDER FOOD
    // ------------------------------------
    @Override
    public void orderFood(Scanner sc) {
        MenuService.printMenu();

        System.out.print("Enter Item ID: ");
        String idItem = sc.nextLine().trim();
        MenuItem item = MenuService.findById(idItem);

        if (item == null) {
            System.out.println("Invalid item!");
            return;
        }

        System.out.print("Quantity: ");
        int qty = readInt(sc);

        double total = item.price * qty;
        double bal = WalletService.getBalance(id);

        System.out.println("Current Wallet Balance: ₹" + bal);
        System.out.println("Total Amount: ₹" + total);

        // ----------------------------
        // DELIVERY OPTION
        // ----------------------------
        System.out.println("\n1) Pickup");
        System.out.println("2) Deliver to Hostel/Class");
        System.out.print("Choose Delivery Method: ");
        int delChoice = readInt(sc);

        String deliveryLocation = "PICKUP";
        if (delChoice == 2) {
            System.out.print("Enter Delivery Location (Hostel/Room/Class): ");
            deliveryLocation = sc.nextLine().trim();
        }

        System.out.println("\nChoose Payment Method:");
        System.out.println("1) Wallet");
        System.out.println("2) UPI QR");
        System.out.print("Enter: ");
        int payChoice = readInt(sc);

        boolean paid = false;
        String payMode = "";

        if (payChoice == 1) {
            if (!WalletService.deduct(id, total)) {
                System.out.println(ConsoleColor.RED +
                        "Insufficient wallet balance!" +
                        ConsoleColor.RESET);
                return;
            }
            paid = true;
            payMode = "WALLET";
        }
        else if (payChoice == 2) {

            String upi = "upi://pay?pa=klcanteen@upi&pn=KL+Canteen&am="
                    + total + "&cu=INR&tn=Food+Order";

            paid = QRCodeGenerator.showQRAndSimulatePayment(upi, total, id);
            payMode = "UPI_QR";
        }
        else {
            System.out.println("Invalid payment choice.");
            return;
        }

        if (paid) {

            // Generate Order ID
            String orderId = OrderService.generateOrderId();

            // Save order with delivery location
            OrderService.saveOrder(
                    orderId,
                    id,
                    "STUDENT",
                    item.name,
                    qty,
                    total,
                    payMode,
                    deliveryLocation
            );

            // Estimated preparation time
            int estTime = 5 + (qty * 2);

            System.out.println(ConsoleColor.GREEN +
                    "Order placed successfully!" +
                    ConsoleColor.RESET);

            System.out.println(ConsoleColor.YELLOW +
                    "Order ID: " + orderId +
                    "\nDelivery: " + deliveryLocation +
                    "\nEstimated Time: " + estTime + " minutes" +
                    ConsoleColor.RESET);

            // Save payment log
            String log = "STUDENT," + id + "," + item.name + "," + qty + "," +
                    total + "," + payMode + "," + new Date();
            FileStorage.appendLine("payments.txt", log);
        }
    }

    // ------------------------------------
    // WALLET
    // ------------------------------------
    private void handleWallet(Scanner sc) {
        double bal = WalletService.getBalance(id);
        System.out.println("Current balance: " + bal);

        System.out.print("Add amount (0 to cancel): ");
        double amt;

        try {
            amt = Double.parseDouble(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amt <= 0) return;

        double newBal = WalletService.addMoney(id, amt);
        System.out.println("New balance: " + newBal);
    }

    // ------------------------------------
    // PAYMENT HISTORY
    // ------------------------------------
    private void showHistory() {
        List<String> lines = FileStorage.readLines("payments.txt");
        boolean found = false;

        for (String l : lines) {
            String[] p = l.split(",");

            if (p.length >= 6 && p[0].equals("STUDENT") && p[1].equals(id)) {
                System.out.println("Item: " + p[2] +
                                   " | Qty: " + p[3] +
                                   " | Total: " + p[4] +
                                   " | Date: " + p[5]);
                found = true;
            }
        }

        if (!found) System.out.println("No payments yet.");
    }
}

