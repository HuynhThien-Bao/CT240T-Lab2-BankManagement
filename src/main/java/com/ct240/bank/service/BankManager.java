package com.ct240.bank.service;

import com.ct240.bank.exception.AccountNotFoundException;
import com.ct240.bank.exception.InsufficientBalanceException;
import com.ct240.bank.exception.InvalidAmountException;
import com.ct240.bank.model.BankAccount;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quản lý các tài khoản ngân hàng theo số tài khoản.
 *
 * <p>Dùng {@code Map<String, BankAccount>} với khóa là số tài khoản để tra cứu nhanh;
 * {@link LinkedHashMap} giữ thứ tự thêm vào khi in danh sách.</p>
 */
public class BankManager {

    private final Map<String, BankAccount> accounts = new LinkedHashMap<>();

    /**
     * Thêm một tài khoản mới.
     *
     * @param account tài khoản cần thêm
     * @throws IllegalArgumentException Tiền điều kiện: account khác null và số tài khoản chưa tồn tại
     * @post-condition findAccount(account.getAccountNumber()) trả về chính account
     */
    public void addAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Tai khoan khong duoc null");
        }
        String accountNumber = account.getAccountNumber();
        if (accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("So tai khoan " + accountNumber + " da ton tai");
        }
        accounts.put(accountNumber, account);
    }

    /**
     * Tìm tài khoản theo số tài khoản.
     *
     * @param accountNumber số tài khoản cần tìm
     * @return tài khoản tương ứng, không bao giờ null
     * @throws AccountNotFoundException nếu không có tài khoản tương ứng
     */
    public BankAccount findAccount(String accountNumber) throws AccountNotFoundException {
        BankAccount account = (accountNumber == null) ? null : accounts.get(accountNumber.trim());
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    /**
     * @return danh sách mọi tài khoản (bản sao chỉ đọc; sửa danh sách này không ảnh hưởng dữ liệu)
     */
    public List<BankAccount> getAllAccounts() {
        return List.copyOf(accounts.values());
    }

    /**
     * Chuyển tiền giữa hai tài khoản.
     *
     * @param fromAcc số tài khoản nguồn
     * @param toAcc   số tài khoản đích
     * @param amount  số tiền cần chuyển
     * @throws AccountNotFoundException     Tiền điều kiện: cả hai tài khoản phải tồn tại
     * @throws IllegalArgumentException     Tiền điều kiện: tài khoản nguồn khác tài khoản đích
     * @throws InvalidAmountException       Tiền điều kiện: amount &gt; 0
     * @throws InsufficientBalanceException Tiền điều kiện: tài khoản nguồn đủ số dư theo quy tắc của loại tài khoản đó
     * @post-condition from.balance_new == from.balance_old - amount và to.balance_new == to.balance_old + amount
     *                 (tổng số dư hai tài khoản không đổi)
     */
    public void transferMoney(String fromAcc, String toAcc, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        BankAccount from = findAccount(fromAcc);
        BankAccount to = findAccount(toAcc);
        if (from.equals(to)) {
            throw new IllegalArgumentException("Tai khoan nguon va tai khoan dich phai khac nhau");
        }
        double totalBefore = from.getBalance() + to.getBalance();

        // withdraw kiểm tra toàn bộ tiền điều kiện (amount > 0, đủ số dư) trước khi đổi số dư.
        // Nếu withdraw ném ngoại lệ thì chưa có gì thay đổi; nếu thành công thì deposit cùng số tiền
        // chắc chắn hợp lệ. Vì vậy giao dịch luôn trọn vẹn: hoặc cả hai bước, hoặc không bước nào.
        from.withdraw(amount);
        to.deposit(amount);

        assert Math.abs(from.getBalance() + to.getBalance() - totalBefore) < 1e-6
                : "Hau dieu kien chuyen tien bi vi pham";
    }

    /**
     * Tính tổng số dư của một danh sách tài khoản.
     *
     * <p><b>PECS - Producer Extends:</b> danh sách chỉ được đọc (đóng vai trò "producer"),
     * nên dùng {@code ? extends BankAccount}. Nhờ vậy truyền được cả {@code List<SavingAccount>},
     * {@code List<CheckingAccount>} lẫn {@code List<BankAccount>}.</p>
     */
    public double calculateTotalBalance(List<? extends BankAccount> accountList) {
        double total = 0.0;
        for (BankAccount acc : accountList) {
            total += acc.getBalance(); // Producer Extends
        }
        return total;
    }

    /** Tổng số dư toàn hệ thống. */
    public double calculateSystemTotalBalance() {
        return calculateTotalBalance(getAllAccounts());
    }

    /**
     * Gom các tài khoản thuộc kiểu {@code type} vào {@code destination}.
     *
     * <p><b>PECS - Consumer Super:</b> {@code destination} chỉ được ghi (đóng vai trò "consumer"),
     * nên dùng {@code ? super T}. Ví dụ gom {@code SavingAccount} vào {@code List<SavingAccount>},
     * {@code List<BankAccount>} hoặc {@code List<Object>} đều được.</p>
     */
    public <T extends BankAccount> void collectAccountsOfType(Class<T> type, Collection<? super T> destination) {
        for (BankAccount account : accounts.values()) {
            if (type.isInstance(account)) {
                destination.add(type.cast(account)); // Consumer Super
            }
        }
    }
}
