package com.ct240.bank;

import com.ct240.bank.exception.AccountNotFoundException;
import com.ct240.bank.exception.InsufficientBalanceException;
import com.ct240.bank.exception.InvalidAmountException;
import com.ct240.bank.model.BankAccount;
import com.ct240.bank.model.CheckingAccount;
import com.ct240.bank.model.SavingAccount;
import com.ct240.bank.service.BankManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chương trình console quản lý tài khoản ngân hàng.
 *
 * <p>Mọi ngoại lệ được bắt tại {@link #run()} và in ra thông báo thân thiện,
 * nên chương trình không bị dừng đột ngột khi người dùng nhập sai.</p>
 */
public class Main {

    private final BankManager manager = new BankManager();
    private final Scanner scanner;

    private Main(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        // try-with-resources: Scanner tự được đóng khi ra khỏi khối try
        try (Scanner scanner = new Scanner(System.in)) {
            Main app = new Main(scanner);
            app.loadSampleData();
            app.run();
        }
    }

    private void run() {
        while (true) {
            printMenu();
            try {
                String choice = readLine("Chon chuc nang: ");
                if (choice.equals("0")) {
                    System.out.println("Tam biet!");
                    return;
                }
                handle(choice);
            } catch (InvalidAmountException e) {
                System.out.println("[Loi so tien] " + e.getMessage());
            } catch (InsufficientBalanceException e) {
                System.out.println("[Khong du so du] " + e.getMessage());
            } catch (AccountNotFoundException e) {
                System.out.println("[Khong tim thay] " + e.getMessage());
            } catch (NumberFormatException e) {
                // Phải đứng trước IllegalArgumentException vì là lớp con của nó
                System.out.println("[Nhap sai] Vui long nhap mot so hop le, vi du 50000");
            } catch (IllegalArgumentException e) {
                System.out.println("[Du lieu khong hop le] " + e.getMessage());
            } catch (EndOfInputException e) {
                System.out.println();
                System.out.println("Khong con du lieu nhap. Ket thuc chuong trinh.");
                return;
            } finally {
                System.out.println("------------------------------------------");
            }
        }
    }

    private void printMenu() {
        System.out.println("===== QUAN LY TAI KHOAN NGAN HANG =====");
        System.out.println("1. Mo tai khoan tiet kiem");
        System.out.println("2. Mo tai khoan thanh toan");
        System.out.println("3. Nap tien");
        System.out.println("4. Rut tien");
        System.out.println("5. Chuyen tien");
        System.out.println("6. Xem thong tin tai khoan");
        System.out.println("7. Danh sach tai khoan");
        System.out.println("8. Tong so du toan he thong");
        System.out.println("9. Tong so du cac tai khoan tiet kiem");
        System.out.println("0. Thoat");
    }

    private void handle(String choice)
            throws InvalidAmountException, InsufficientBalanceException, AccountNotFoundException {
        switch (choice) {
            case "1" -> openSavingAccount();
            case "2" -> openCheckingAccount();
            case "3" -> deposit();
            case "4" -> withdraw();
            case "5" -> transfer();
            case "6" -> showAccount();
            case "7" -> listAccounts();
            case "8" -> System.out.println("Tong so du toan he thong: " + money(manager.calculateSystemTotalBalance()));
            case "9" -> showSavingTotal();
            default -> System.out.println("Chuc nang khong hop le. Vui long chon tu 0 den 9.");
        }
    }

    // ===================== Các chức năng =====================

    private void openSavingAccount() {
        String number = readLine("So tai khoan: ");
        String name = readLine("Ten chu tai khoan: ");
        double initialBalance = readNumber("So du ban dau (toi thieu 50000): ");
        double interestRate = readNumber("Lai suat nam (vi du 0.05 la 5%): ");
        manager.addAccount(new SavingAccount(number, name, initialBalance, interestRate));
        System.out.println("Da mo tai khoan tiet kiem " + number);
    }

    private void openCheckingAccount() {
        String number = readLine("So tai khoan: ");
        String name = readLine("Ten chu tai khoan: ");
        double initialBalance = readNumber("So du ban dau: ");
        manager.addAccount(new CheckingAccount(number, name, initialBalance));
        System.out.println("Da mo tai khoan thanh toan " + number);
    }

    private void deposit() throws AccountNotFoundException, InvalidAmountException {
        BankAccount account = manager.findAccount(readLine("So tai khoan: "));
        double amount = readNumber("So tien nap: ");
        account.deposit(amount);
        System.out.println("Nap tien thanh cong. So du moi: " + money(account.getBalance()));
    }

    private void withdraw() throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        BankAccount account = manager.findAccount(readLine("So tai khoan: "));
        double amount = readNumber("So tien rut: ");
        account.withdraw(amount); // Đa hình: mỗi loại tài khoản áp quy tắc rút tiền riêng
        System.out.println("Rut tien thanh cong. So du moi: " + money(account.getBalance()));
    }

    private void transfer() throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        String from = readLine("Tai khoan nguon: ");
        String to = readLine("Tai khoan dich: ");
        double amount = readNumber("So tien chuyen: ");
        manager.transferMoney(from, to, amount);
        System.out.println("Chuyen tien thanh cong.");
        System.out.println("  " + manager.findAccount(from));
        System.out.println("  " + manager.findAccount(to));
    }

    private void showAccount() throws AccountNotFoundException {
        BankAccount account = manager.findAccount(readLine("So tai khoan: "));
        System.out.println("Loai tai khoan : " + typeName(account));
        System.out.println("So tai khoan   : " + account.getAccountNumber());
        System.out.println("Chu tai khoan  : " + account.getHolderName());
        System.out.println("So du          : " + money(account.getBalance()));
        System.out.println("So du toi thieu: " + money(account.getMinimumBalance()));
        if (account instanceof SavingAccount saving) {
            System.out.printf("Lai suat nam   : %.2f%%%n", saving.getInterestRate() * 100);
        }
    }

    private void listAccounts() {
        List<BankAccount> all = manager.getAllAccounts();
        if (all.isEmpty()) {
            System.out.println("Chua co tai khoan nao.");
            return;
        }
        System.out.printf("%-10s %-12s %-22s %20s%n", "So TK", "Loai", "Chu tai khoan", "So du");
        for (BankAccount account : all) {
            System.out.printf("%-10s %-12s %-22s %20s%n", account.getAccountNumber(), typeName(account),
                    account.getHolderName(), money(account.getBalance()));
        }
    }

    private void showSavingTotal() {
        List<SavingAccount> savings = new ArrayList<>();
        manager.collectAccountsOfType(SavingAccount.class, savings); // ? super T: List<SavingAccount> nhận SavingAccount
        double total = manager.calculateTotalBalance(savings);       // ? extends BankAccount: nhận List<SavingAccount>
        System.out.println("So tai khoan tiet kiem: " + savings.size() + ", tong so du: " + money(total));
    }

    // ===================== Nhập liệu và tiện ích =====================

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EndOfInputException();
        }
        return scanner.nextLine().trim();
    }

    /**
     * @throws NumberFormatException nếu người dùng nhập không phải số
     */
    private double readNumber(String prompt) {
        return Double.parseDouble(readLine(prompt));
    }

    private static String typeName(BankAccount account) {
        return (account instanceof SavingAccount) ? "Tiet kiem" : "Thanh toan";
    }

    private static String money(double amount) {
        return String.format("%,.0f VND", amount);
    }

    private void loadSampleData() {
        manager.addAccount(new SavingAccount("TK001", "Nguyen Van A", 1_000_000, 0.05));
        manager.addAccount(new CheckingAccount("TK002", "Tran Thi B", 500_000));
        System.out.println("Da tao san 2 tai khoan mau: TK001 (tiet kiem) va TK002 (thanh toan)");
    }

    /** Báo hiệu hết dữ liệu nhập, ví dụ khi chạy bằng file input hoặc đóng luồng nhập. */
    private static final class EndOfInputException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
