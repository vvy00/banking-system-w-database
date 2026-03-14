public class Account {
    int id;
    String pin;
    String name;
    double balance;
    boolean isAdmin;

    public Account(int id, String pin, String name, double balance, boolean isAdmin) {
        this.id = id;
        this.pin = pin;
        this.name = name;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    // New helper methods
    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false; // Not enough money
    }
}