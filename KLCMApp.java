import java.util.*;

public class KLCMApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        LogoPrinter.printLogo();

        System.out.println("1 - Student");
        System.out.println("2 - Faculty");
        System.out.println("3 - Admin");
        System.out.println("4 - Guest");
        System.out.print("Enter your choice: ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid main choice.");
            sc.close();
            return;
        }

        BaseUser user = null;

        switch (choice) {
            case 1 -> {
                System.out.print("Enter RegId: ");
                String reg = sc.nextLine();
                System.out.print("Password: ");
                String pass = sc.nextLine();
                if (!pass.equals("student@123") || reg.length() != 10) {
                    System.out.println("Invalid student credentials.");
                } else {
                    user = new StudentUser(reg);
                }
            }
            case 2 -> {
                System.out.print("Enter Faculty Mail: ");
                String mail = sc.nextLine();
                System.out.print("Password: ");
                String pass = sc.nextLine();
                if (!pass.equals("Faculty@123") || !mail.contains("@klh.edu.in")) {
                    System.out.println("Invalid faculty credentials.");
                } else {
                    System.out.print("Enter Employee Code: ");
                    String emp = sc.nextLine();
                    user = new FacultyUser(emp);
                }
            }
            case 3 -> {
                System.out.print("Enter Admin Mail: ");
                String mail = sc.nextLine();
                System.out.print("Password: ");
                String pass = sc.nextLine();
                if (!pass.equals("Admin@123")) {
                    System.out.println("Invalid admin login.");
                } else {
                    user = new AdminUser(mail);
                }
            }
            case 4 -> {
                System.out.print("Enter Phone: ");
                String phone = sc.nextLine();
                System.out.print("Enter Name: ");
                String name = sc.nextLine();
                user = new GuestUser(phone, name);
            }
            default -> System.out.println("Invalid main choice.");
        }

        if (user != null) {
            user.showPortal(sc);
        }

        sc.close();
        System.out.println("Exiting application...");
    }
}
