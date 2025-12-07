import java.util.*;

public class AlgoLab {

    // recursion: factorial
    public static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    // recursion: fibonacci
    public static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // 2D matrix demo: sales[day][item]
    public static void showSalesMatrixDemo() {
        int[][] sales = {
            {5, 2, 7},   // day 1
            {3, 4, 1},   // day 2
            {6, 1, 2},   // day 3
            {4, 5, 3},   // day 4
            {2, 3, 4},   // day 5
            {7, 2, 5},   // day 6
            {1, 4, 6}    // day 7
        };

        System.out.println("Sales matrix (rows=days, cols=items):");
        for (int[] row : sales) {
            for (int v : row) {
                System.out.print(v + " ");
            }
            System.out.println();
        }

        // row sums
        System.out.println("Total sales per day:");
        for (int i = 0; i < sales.length; i++) {
            int sum = 0;
            for (int j = 0; j < sales[i].length; j++) sum += sales[i][j];
            System.out.println("Day " + (i+1) + ": " + sum);
        }
    }

    // bitwise operations demo
    public static void bitwiseDemo(int a, int b) {
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a & b = " + (a & b));
        System.out.println("a | b = " + (a | b));
        System.out.println("a ^ b = " + (a ^ b));
        System.out.println("a << 1 = " + (a << 1));
        System.out.println("a >> 1 = " + (a >> 1));
    }

    // string analyzer
    public static void analyzeString(String s) {
        String lower = s.toLowerCase();
        int vowels = 0;
        for (char c : lower.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) vowels++;
        }
        String[] words = s.trim().isEmpty() ? new String[0] : s.trim().split("\\s+");
        System.out.println("Total characters: " + s.length());
        System.out.println("Total words: " + words.length);
        System.out.println("Total vowels: " + vowels);
        System.out.println("Contains 'bad' word? " + lower.contains("bad"));
    }
}
