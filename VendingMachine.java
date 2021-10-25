package com.vmachine;

import java.sql.*;
import java.util.Scanner;

public class VendingMachine {
	static Connection conn;
	static Scanner sc;
	static String alert; // To give alert to the admin if any product exhausted.
	// ======================MAIN METHOD===============================

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vm_items", "root", "");

			sc = new Scanner(System.in);
			System.out.println("1) Press 1 if you are a Customer.");
			System.out.println("2) Press 2 if you are an Admin.");
			int n = Integer.parseInt(sc.nextLine());
			if (n == 1) {
				boolean t = true;
				while (t) {
					alert = customer();
					System.out.println("If you have any query, then talk to our executive at 1234567890 Thank you...");
					System.out.println("Do you want to continue press 1 else 0");
					int o = Integer.parseInt(sc.nextLine());
					if (o == 0)
						t = false;
				}
			}
			if (n == 2) {
				boolean t = true;
				while (t) {
					admin();
					System.out.println("Do you want to continue press 1 else 0");
					int o = Integer.parseInt(sc.nextLine());
					if (o == 0)
						t = false;
				}
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				if (alert != null) {
					alertAdmin(alert);
				}
				sc.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// =================CUSTOMER METHOD===============================
	public static String customer() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from items");
		System.out.println("Item ID\t\tItem Name\tItem Quantity\tItem Price");
		while (rs.next())
			System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t" + rs.getInt(4));
		System.out.println("\n");
		System.out.println("These are the products available...");
		System.out.println("Please enter the Item ID of the product which you want to purchase: ");
		String id = sc.nextLine();
		vmUpdate(id);
		return id;
	}

	// =====================ADMIN METHOD================================
	public static void admin() throws SQLException {
		final int pass = 1234;
		sc = new Scanner(System.in);
		System.out.println("Enter password: ");
		int password = Integer.parseInt(sc.nextLine());
		if (password == pass) {
			System.out.println("Access granted...");
			System.out.println("1) Press 1 to Add new product to the database.");
			System.out.println("2) Press 2 to update the quantity of an item in the database.");
			System.out.println("3) Press 3 to view the database.");
			System.out.println("Enter your choice: ");
			int n = Integer.parseInt(sc.nextLine());
			if (n == 1)
				addItem();
			if (n == 2)
				updateItem();
			if (n == 3) {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from items");
				System.out.println("Item ID\t\tItem Name\tItem Quantity\tItem Price");
				while (rs.next())
					System.out.println(
							rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t" + rs.getInt(4));
				System.out.println("\n");
			}

		} else {
			System.out.println("Access denied...");
			return;
		}
	}

	// ======================DATABASE UPDATE METHOD IF CUSTOMER PERFORMS ANY TRANSACTION==========================
	public static void vmUpdate(String id) throws SQLException {

		String sql = "SELECT ItemQuantity, ItemPrice from items WHERE ItemId=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		rs.next();
		int quantity = rs.getInt(1);
		int price = rs.getInt(2);
		if (quantity <= 0) {
			System.out.println("Sorry the product is out of stock...");
			return;
		}
		int paid = doPayment(id);
		if (paid < price) {
			System.out.println("Payment failed reverting the money back...");
			revertMoney(price, paid);
			System.out.println("You are one of our valuable customers we're sorry for the inconvenience...");
			return;
		}
		String sql1 = "UPDATE items SET ItemQuantity = ItemQuantity-1 WHERE ItemId=?";
		PreparedStatement statement1 = conn.prepareStatement(sql1);
		statement1.setString(1, id);
		statement1.executeUpdate();
		if (paid > price)
			revertMoney(price, paid);
		System.out.println("Thank you for the purchase we hope to see you again soon...");
	}

	// =====================PAYMENT METHOD===============================
	public static int doPayment(String id) throws SQLException {
		System.out.println("You can do payment in following denominations: ");
		System.out.println("Re1\tRs2\tRs5\tRs10");
		String sql = "SELECT ItemPrice from items WHERE ItemId=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		rs.next();
		int price = rs.getInt(1);
		System.out.println("You will get total Twenty attempts to enter the total sum of money...");
		sc = new Scanner(System.in);
		int totalmoney = 0;
		int count = 0;
		while (count <= 20 && totalmoney < price) {
			System.out.println("Enter coin: ");
			int m = Integer.parseInt(sc.nextLine());
			if (m == 1 || m == 2 || m == 5 || m == 10) {
				totalmoney += m;
				count += 1;
			} else
				System.out.println("Invalid coin...");
			count += 1;
		}
		return totalmoney;
	}

	// =====================MONEY REVERT or REFUND METHOD=====================
	public static void revertMoney(int price, int paid) {
		if (paid < price) {
			int m = paid;
			moneyCalculator(m);
			System.out.println("Total amount you paid was: " + paid);
		}
		if (paid > price) {
			int amounttobereturned = paid - price;
			int n = amounttobereturned;
			moneyCalculator(n);
			System.out
					.println("Total amount you paid: " + paid + " Extra amount paid by you was: " + amounttobereturned);
		}
	}

	// =================TO ADD NEW PRODUCT TO THE DATABASE====================
	public static void addItem() throws SQLException {
		sc = new Scanner(System.in);
		String sql = "INSERT INTO items " + "(ItemId , ItemName, ItemQuantity, ItemPrice) " + "VALUES (?, ?, ?, ?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		System.out.println("Enter Item id: ");
		String itemid = sc.nextLine();
		System.out.println("Enter Item name: ");
		String itemname = sc.nextLine();
		System.out.println("Enter Item quantity: ");
		String itemquantity = sc.nextLine();
		System.out.println("Enter Item price: ");
		String itemprice = sc.nextLine();
		statement.setString(1, itemid);
		statement.setString(2, itemname);
		statement.setString(3, itemquantity);
		statement.setString(4, itemprice);
		int rowsInserted = statement.executeUpdate();
		if (rowsInserted > 0) {
			System.out.println("A new item inserted successfully!");
		}
	}

	// =======================TO UPDATE ITEM QUANTITY====================================
	public static void updateItem() throws SQLException {
		sc = new Scanner(System.in);
		System.out.println("Enter the item id in which more quantity is to be added: ");
		String id = sc.nextLine();
		System.out.println("Enter the quantity to be added: ");
		String quantity = sc.nextLine();
		String sql = "UPDATE items SET ItemQuantity = ItemQuantity + ? WHERE ItemId=?";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, quantity);
		statement.setString(2, id);
		int rowsUpdated = statement.executeUpdate();
		if (rowsUpdated > 0) {
			System.out.println("An existing item quantity updated successfully!");
		}
	}

	// ==================ALERT THE ADMIN IF ANY PRODUCT EXAHUSTED============================
	public static void alertAdmin(String alert) throws SQLException {
		String sql2 = "SELECT ItemQuantity from items WHERE ItemId=?";
		PreparedStatement statement2 = conn.prepareStatement(sql2);
		statement2.setString(1, alert);
		ResultSet rs2 = statement2.executeQuery();
		rs2.next();
		int quantity2 = rs2.getInt(1);
		if (quantity2 == 0) {
			System.out.println("Hey! Admin product with Item_Id = " + alert + " is exahusted please add more...");
		}
	}

	// ==================CALCULATES AMOUNT TO BE REFUNDED=========================
	public static void moneyCalculator(int amount) {
		int count10 = 0;
		int count5 = 0;
		int count2 = 0;
		int count1 = 0;
		int m = amount;
		while (m >= 10) {
			if (m % 10 == 0)
				count10 += m / 10;
			else
				count10 += 1;
			m %= 10;
		}
		while (m >= 5) {
			if (m % 5 == 0)
				count5 += m / 5;
			else
				count5 += 1;
			m %= 5;
		}
		while (m >= 2) {
			if (m % 2 == 0)
				count2 += m / 2;
			else
				count2 += 1;
			m %= 2;
		}
		while (m >= 1) {
			m %= 1;
			count1 += 1;
		}
		int totalmoneyreverted = 10 * count10 + 5 * count5 + 2 * count2 + 1 * count1;
		System.out.println("10 X " + count10 + " + 5 X " + count5 + " + 2 X " + count2 + " + 1 X " + count1);
		System.out.println("Total amount reverted: " + totalmoneyreverted);
	}
}
