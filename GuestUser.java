import java.util.*;

public class GuestUser extends BaseUser {

    public GuestUser(String phone, String name) {
        super(phone, name, "GUEST");
    }

    @Override
    public void showPortal(Scanner sc) {

        // -----------------------------
        //  OTP AUTHENTICATION
        // -----------------------------
        int otp = (int)(Math.random() * 900000) + 100000;
        System.out.println("OTP sent (debug): " + otp);

        System.out.print("Enter OTP: ");
        int entered = readInt(sc);

        if (entered != otp) {
            System.out.println(ConsoleColor.RED + "Invalid OTP!" + ConsoleColor.RESET);
            return;
        }

        logLogin();
        System.out.println(ConsoleColor.CYAN + "\n--- GUEST PORTAL ---" + ConsoleColor.RESET);

        int choice;
        do {
            System.out.println("\n1) View Menu");
            System.out.println("2) Order Food");
            System.out.println("3) Track My Orders");
            System.out.println("0) Exit");
            System.out.print("Enter: ");

            choice = readInt(sc);

            switch (choice) {
                case 1 -> MenuService.printMenu();
                case 2 -> orderFood(sc);
                case 3 -> OrderService.showUserOrders(id);
                case 0 -> System.out.println("Exiting guest portal...");
                default -> System.out.println("Invalid!");
            }

        } while (choice != 0);
    }

    // ---------------------------------------
    //  ORDER FOOD (UPI QR + DELIVERY + ETA)
    // ---------------------------------------
    private void orderFood(Scanner sc) {

        MenuService.printMenu();

        System.out.print("Enter Item ID: ");
        String idItem = sc.nextLine().trim();

        MenuItem item = MenuService.findById(idItem);

        if (item == null) {
            System.out.println(ConsoleColor.RED + "Invalid Item!" + ConsoleColor.RESET);
            return;
        }

        System.out.print("Quantity: ");
        int qty = readInt(sc);

        double total = item.price * qty;

        System.out.println("Total Amount: ₹" + total);

        // ---------------------------------------
        // DELIVERY OPTION
        // ---------------------------------------
        System.out.println("\nDelivery Option:");
        System.out.println("1) Pickup");
        System.out.println("2) Deliver to Location");
        System.out.print("Choose: ");
        int deliveryOption = readInt(sc);

        String deliveryLocation = "PICKUP";

        if (deliveryOption == 2) {
            System.out.print("Enter Delivery Location / Cabin No: ");
            deliveryLocation = sc.nextLine().trim();
        }

        // ---------------------------------------
        // PAY USING QR
        // ---------------------------------------
        String upi = "upi://pay?pa=klcanteen@upi&pn=KL+Canteen&am=" +
                total + "&cu=INR&tn=Guest+Order";

        boolean paid = QRCodeGenerator.showQRAndSimulatePayment(upi, total, id);

        if (!paid) {
            System.out.println(ConsoleColor.RED + "Payment failed!" + ConsoleColor.RESET);
            return;
        }

        // ---------------------------------------
        // SAVE ORDER
        // ---------------------------------------
        String orderId = OrderService.generateOrderId();

        OrderService.saveOrder(
                orderId,
                id,
                "GUEST",
                item.name,
                qty,
                total,
                "UPI_QR",
                deliveryLocation   // NEW FIELD
        );

        // ---------------------------------------
        // SAVE PAYMENT LOG
        // ---------------------------------------
        String log = "GUEST," + id + "," + item.name + "," + qty + "," +
                total + ",UPI_QR," + new Date();
        FileStorage.appendLine("payments.txt", log);

        // ---------------------------------------
        // ESTIMATED TIME
        // ---------------------------------------
        int est = 5 + (qty * 2);

        System.out.println(ConsoleColor.GREEN +
                "\nOrder Placed Successfully!" + ConsoleColor.RESET);

        System.out.println(ConsoleColor.YELLOW +
                "Order ID: " + orderId +
                "\nDelivery: " + deliveryLocation +
                "\nEstimated Time: " + est + " minutes" +
                ConsoleColor.RESET);
    }
}


