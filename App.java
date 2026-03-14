import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.sql.*;

public class App {
    static ArrayList<Account> allAccounts = new ArrayList<>();
    static Connection conn;

    public static void main(String[] args) {
        // Connect to database
        try {
            conn = connect();
            if (conn != null) {
                System.out.println(">>> [SUCCESS] Connected to MySQL Database.");
            }
        } catch (Exception e) {
            System.out.println(">>> [ERROR] Database Connection Failed: " + e.getMessage());
            return;
        }

        // Load data
        allAccounts = loadAccounts();

        // UI loop
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n=== JAVA BANKING SYSTEM (SQL VERSION) ===");

        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Customer Login");
            System.out.println("2. Admin Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            
            int choice = scanner.nextInt();

            if (choice == 1) {
                customerMenu(scanner);
            } else if (choice == 2) {
                adminMenu(scanner);
            } else if (choice == 3) {
                running = false;
            }
        }

        System.out.println("Session Ended. Goodbye!");
        scanner.close();
    }
    // Customer menu
    public static void customerMenu(Scanner sc) {
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();
        System.out.print("Enter PIN: ");
        String pin = sc.next();

        Account current = findAccount(id, pin);

        if (current != null && !current.isAdmin) {
            System.out.println("\nWelcome, " + current.name);
            boolean session = true;
            while (session) {
                System.out.println("\n1. Check Balance\n2. Deposit\n3. Withdraw\n4. Logout");
                int action = sc.nextInt();

                if (action == 1) {
                    showBalance(current.id);
                } else if (action == 2) {
                    System.out.print("Deposit Amount: ");
                    updateBalance(current.id, sc.nextDouble());
                } else if (action == 3) {
                    System.out.print("Withdraw Amount: ");
                    updateBalance(current.id, -sc.nextDouble());
                } else {
                    session = false;
                }
            }
        } else {
            System.out.println("Invalid ID or PIN!");
        }
    }
    // Admin menu
    public static void adminMenu(Scanner sc) {
        System.out.print("Enter Admin ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Admin PIN: ");
        String pin = sc.next();

        Account current = findAccount(id, pin);

        if (current != null && current.isAdmin) {
            boolean adminSession = true;
            while (adminSession) {
                System.out.println("\n--- ADMIN DASHBOARD ---");
                System.out.println("1. Open Account\n2. Close Account\n3. Modify Account\n4. View All Accounts\n5. Logout");
                int action = sc.nextInt();

                if (action == 1) openAccount(sc);
                else if (action == 2) closeAccount(sc);
                else if (action == 3) modifyAccount(sc);
                else if (action == 4) viewAllAccounts();
                else adminSession = false;
            }
        } else {
            System.out.println("Access Denied!");
        }
    }

    // SQL database operations
    public static void showBalance(int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) System.out.println("Current Balance: $" + rs.getDouble("balance"));
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static void updateBalance(int id, double amount) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Transaction Updated in Database.");
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static void openAccount(Scanner sc) {
        try {
            System.out.print("ID: "); int id = sc.nextInt();
            System.out.print("PIN: "); String pin = sc.next();
            System.out.print("Name: "); sc.nextLine(); String name = sc.nextLine();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?, 0.0, false)");
            pstmt.setInt(1, id); pstmt.setString(2, pin); pstmt.setString(3, name);
            pstmt.executeUpdate();
            System.out.println("Account Created.");
            allAccounts = loadAccounts(); // Refresh the list
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static void closeAccount(Scanner sc) {
        try {
            System.out.print("ID to close: "); int id = sc.nextInt();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM accounts WHERE id = ?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Account Closed.");
            allAccounts = loadAccounts();
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static void modifyAccount(Scanner sc) {
        try {
            System.out.print("ID to modify: "); int id = sc.nextInt();
            System.out.print("New Name: "); sc.nextLine(); String name = sc.nextLine();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE accounts SET name = ? WHERE id = ?");
            pstmt.setString(1, name); pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Account Modified.");
            allAccounts = loadAccounts();
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static void viewAllAccounts() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM accounts");
            System.out.println("\nID | Name | Balance | Admin");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | $" + rs.getDouble("balance") + " | " + rs.getBoolean("is_admin"));
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    public static Connection connect() throws Exception {
        String url = "jdbc:mysql://localhost:3306/BankDB?allowPublicKeyRetrieval=true&useSSL=false";
        String user = "root";
        String password = "MySQL80!";
        return DriverManager.getConnection(url, user, password);
    }

    public static ArrayList<Account> loadAccounts() {
        ArrayList<Account> list = new ArrayList<>();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM accounts");
            while (rs.next()) {
                list.add(new Account(rs.getInt("id"), rs.getString("pin"), rs.getString("name"), rs.getDouble("balance"), rs.getBoolean("is_admin")));
            }
        } catch (Exception e) { System.out.println("DB Load failed."); }
        return list;
    }
    public static Account findAccount(int id, String pin) {
        for (Account a : allAccounts) {
            if (a.id == id && a.pin.equals(pin)) return a;
        }
        return null;
    }
}