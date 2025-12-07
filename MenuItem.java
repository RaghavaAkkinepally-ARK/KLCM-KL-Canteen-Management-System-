public class MenuItem {
    public String id;
    public String name;
    public double price;

    // NEW FIELDS
    public int stock;
    public int threshold;

    // Default stock & threshold version
    public MenuItem(String id, String name, double price) {
        this(id, name, price, 50, 10);  // Default: 50 stock, threshold 10
    }

    // Full constructor with stock + threshold
    public MenuItem(String id, String name, double price, int stock, int threshold) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
    }

    // Used when saving to menu.txt
    @Override
    public String toString() {
        return id + "," + name + "," + price + "," + stock + "," + threshold;
    }
}
