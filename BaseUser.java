import java.util.*;

public abstract class BaseUser {
    protected String id;
    protected String name;
    protected String role;

    public BaseUser(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    protected void logLogin() {
        String line = role + "," + id + "," + name + "," + new Date();
        FileStorage.appendLine("logins.txt", line);
    }

    protected int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    public abstract void showPortal(Scanner sc);
}

