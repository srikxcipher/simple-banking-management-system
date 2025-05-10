import java.sql.*;
import java.util.Scanner;

public class BankingManagementSystem {
    static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1"; 
    static final String USER = "C##SRIK";  
    static final String PASS = "user";    

    static Connection conn;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to the database!");
            //conn.setAutoCommit(false);
            int choice;
            do {
                System.out.println("\n=== Banking Management System ===");
                System.out.println("1. Show all customer records");
                System.out.println("2. Add Customer Record");
                System.out.println("3. Delete Customer Record");
                System.out.println("4. Update Customer Record (Name, Phone, City)");
                System.out.println("5. Show Account Details of a Customer");
                System.out.println("6. Show Loan Details of a Customer");
                System.out.println("7. Deposit Money to an Account");
                System.out.println("8. Withdraw Money from an Account");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 1 -> showAllCustomers();
                    case 2 -> addCustomer();
                    case 3 -> deleteCustomer();
                    case 4 -> updateCustomer();
                    case 5 -> showAccountDetails();
                    case 6 -> showLoanDetails();
                    case 7 -> depositMoney();
                    case 8 -> withdrawMoney();
                    case 9 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice != 9);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void showAllCustomers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER");
        while (rs.next()) {
            System.out.printf("%s | %s | %s | %s\n",
                    rs.getString("CUST_NO"),
                    rs.getString("NAME"),
                    rs.getString("PHONE_NO"),
                    rs.getString("CITY"));
        }
    }

    static void addCustomer() throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter City: ");
        String city = scanner.nextLine();

        String sql = "INSERT INTO CUSTOMER VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, custNo);
        pstmt.setString(2, name);
        pstmt.setString(3, phone);
        pstmt.setString(4, city);
        int rows = pstmt.executeUpdate();
        System.out.println(rows > 0 ? "Customer added." : "Insert failed.");
    }

    static void deleteCustomer() throws SQLException {
        System.out.print("Enter Customer Number to delete: ");
        String custNo = scanner.nextLine();
        String sql = "DELETE FROM CUSTOMER WHERE CUST_NO = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, custNo);
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            //conn.commit();  // ✅ Commit after delete
            System.out.println("Customer deleted.");
        } else {
            System.out.println("Customer not found.");
        }
        pstmt.setQueryTimeout(10);  // 10 seconds timeout
    }
    

    static void updateCustomer() throws SQLException {
    System.out.print("Enter Customer Number to update: ");
    String custNo = scanner.nextLine();

    // Update Name
    System.out.print("Do you want to update the Name? (yes/no): ");
    String choice = scanner.nextLine().trim().toLowerCase();
    if (choice.equals("yes")) {
        System.out.print("New Name: ");
        String name = scanner.nextLine();
        PreparedStatement ps1 = conn.prepareStatement("UPDATE CUSTOMER SET NAME = ? WHERE CUST_NO = ?");
        ps1.setString(1, name);
        ps1.setString(2, custNo);
        ps1.executeUpdate();
    }

    // Update Phone Number
    System.out.print("Do you want to update the Phone Number? (yes/no): ");
    choice = scanner.nextLine().trim().toLowerCase();
    if (choice.equals("yes")) {
        System.out.print("New Phone Number: ");
        String phone = scanner.nextLine();
        PreparedStatement ps2 = conn.prepareStatement("UPDATE CUSTOMER SET PHONE_NO = ? WHERE CUST_NO = ?");
        ps2.setString(1, phone);
        ps2.setString(2, custNo);
        ps2.executeUpdate();
    }

    // Update City
    System.out.print("Do you want to update the City? (yes/no): ");
    choice = scanner.nextLine().trim().toLowerCase();
    if (choice.equals("yes")) {
        System.out.print("New City: ");
        String city = scanner.nextLine();
        PreparedStatement ps3 = conn.prepareStatement("UPDATE CUSTOMER SET CITY = ? WHERE CUST_NO = ?");
        ps3.setString(1, city);
        ps3.setString(2, custNo);
        ps3.executeUpdate();
    }

    System.out.println("Customer update process completed.");
}


    static void showAccountDetails() throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE CUST_NO = ?");
        ps.setString(1, custNo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.printf("%s | %s | %s | %.2f | %s\n",
                    rs.getString("ACCT_NO"),
                    rs.getString("CUST_NO"),
                    rs.getString("TYPE"),
                    rs.getDouble("BALANCE"),
                    rs.getString("BRANCH_CODE"));
        }
    }

    static void showLoanDetails() throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM LOAN WHERE CUST_NO = ?");
        ps.setString(1, custNo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.printf("%s | %s | %.2f | %s\n",
                    rs.getString("LOAN_NO"),
                    rs.getString("CUST_NO"),
                    rs.getDouble("LOAN_AMOUNT"),
                    rs.getString("BRANCH_CODE"));
        }
    }

    static void depositMoney() throws SQLException {
        System.out.print("Enter Account Number: ");
        String acctNo = scanner.nextLine();
        System.out.print("Amount to deposit: ");
        double amt = scanner.nextDouble();
        scanner.nextLine(); // clear buffer

        PreparedStatement ps = conn.prepareStatement("UPDATE ACCOUNT SET BALANCE = BALANCE + ? WHERE ACCT_NO = ?");
        ps.setDouble(1, amt);
        ps.setString(2, acctNo);
        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Deposit successful." : "Account not found.");
    }

    static void withdrawMoney() throws SQLException {
        System.out.print("Enter Account Number: ");
        String acctNo = scanner.nextLine();
        System.out.print("Amount to withdraw: ");
        double amt = scanner.nextDouble();
        scanner.nextLine();

        PreparedStatement check = conn.prepareStatement("SELECT BALANCE FROM ACCOUNT WHERE ACCT_NO = ?");
        check.setString(1, acctNo);
        ResultSet rs = check.executeQuery();
        if (rs.next()) {
            double balance = rs.getDouble("BALANCE");
            if (balance >= amt) {
                PreparedStatement ps = conn.prepareStatement("UPDATE ACCOUNT SET BALANCE = BALANCE - ? WHERE ACCT_NO = ?");
                ps.setDouble(1, amt);
                ps.setString(2, acctNo);
                ps.executeUpdate();
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }
}
