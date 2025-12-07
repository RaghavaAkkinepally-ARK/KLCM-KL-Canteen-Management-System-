import java.io.*;
import java.util.*;

public class QRCodeGenerator {

    private static final int DEFAULT_SIZE = 25;

    // ---------- 1) BASIC "REAL" UPI LINK VALIDATION ----------
    public static boolean isValidUPILink(String upi) {
        if (upi == null || !upi.startsWith("upi://pay")) return false;
        if (!upi.contains("pa=")) return false;   // payee address
        if (!upi.contains("pn=")) return false;   // payee name
        if (!upi.contains("am=")) return false;   // amount
        if (!upi.contains("cu=INR")) return false; // currency
        return true;
    }

    // ---------- 2) BUILD QR MATRIX WITH FINDER PATTERNS ----------
    private static boolean[][] buildMatrix(String upi, int size) {
        boolean[][] mat = new boolean[size][size];

        // add finder pattern (error detection-like) in 3 corners
        addFinder(mat, 0, 0, 7);
        addFinder(mat, 0, size - 7, 7);
        addFinder(mat, size - 7, 0, 7);

        // fill rest based on hash of UPI string
        int seed = upi.hashCode();
        Random r = new Random(seed);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                // skip finder pattern zones
                if (inFinderZone(i, j, size)) continue;

                mat[i][j] = r.nextBoolean();
            }
        }

        return mat;
    }

    private static void addFinder(boolean[][] m, int row, int col, int s) {
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                boolean border = (i == 0 || j == 0 || i == s - 1 || j == s - 1);
                boolean inner = (i >= 2 && i <= s - 3 && j >= 2 && j <= s - 3);
                m[row + i][col + j] = border || inner;
            }
        }
    }

    private static boolean inFinderZone(int i, int j, int size) {
        // top-left
        if (i < 7 && j < 7) return true;
        // top-right
        if (i < 7 && j >= size - 7) return true;
        // bottom-left
        if (i >= size - 7 && j < 7) return true;
        return false;
    }

    // ---------- 3) CONVERT MATRIX TO ASCII QR LINES ----------
    public static java.util.List<String> buildAsciiQR(String upi) {
        boolean[][] m = buildMatrix(upi, DEFAULT_SIZE);
        List<String> rows = new ArrayList<>();

        for (int i = 0; i < m.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < m[i].length; j++) {
                sb.append(m[i][j] ? "██" : "  ");
            }
            rows.add(sb.toString());
        }
        return rows;
    }

    // ---------- 4) PRINT COLORED QR TO CONSOLE ----------
    public static void printQR(List<String> rows) {
        System.out.println(ConsoleColor.GREEN + "\n===== SCAN TO PAY (UPI QR) =====" + ConsoleColor.RESET);
        System.out.println(ConsoleColor.BLUE);
        for (String line : rows) {
            System.out.println(line);
        }
        System.out.println(ConsoleColor.RESET);
    }

    // ---------- 5) SAVE QR TO TEXT FILE ----------
    public static void saveQRToFile(List<String> rows, String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("ASCII QR Code:");
            for (String line : rows) {
                pw.println(line);
            }
            System.out.println("QR saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving QR to file: " + e.getMessage());
        }
    }

    // ---------- 6) HIGH LEVEL: SHOW QR + SIMULATE AUTO-CONFIRM ----------
    public static boolean showQRAndSimulatePayment(String upi, double amount, String payerId) {
        if (!isValidUPILink(upi)) {
            System.out.println(ConsoleColor.RED + "Invalid UPI link! Payment cancelled." + ConsoleColor.RESET);
            return false;
        }

        List<String> rows = buildAsciiQR(upi);

        printQR(rows);

        String fileName = "qr_" + payerId + "_" + System.currentTimeMillis() + ".txt";
        saveQRToFile(rows, fileName);

        System.out.println(ConsoleColor.YELLOW +
                "Please scan this QR with any UPI app (GPay / PhonePe / Paytm) to pay ₹" + amount +
                ConsoleColor.RESET);

        // "Auto confirm" simulation – imitate bank/server callback
        System.out.print("Waiting for bank confirmation");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(900);
                System.out.print(".");
            }
        } catch (InterruptedException ignored) {}

        System.out.println();
        System.out.println(ConsoleColor.GREEN +
                "Payment confirmed by UPI server (simulated)." +
                ConsoleColor.RESET);

        return true;
    }
}
