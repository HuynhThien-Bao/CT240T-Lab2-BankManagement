package com.ct240.bank.model;

import com.ct240.bank.exception.InsufficientBalanceException;
import com.ct240.bank.exception.InvalidAmountException;

/**
 * Tài khoản ngân hàng (lớp trừu tượng).
 *
 * <p><b>Đặc tả kỹ thuật (Design by Contract):</b></p>
 * <ul>
 *   <li><b>Bất biến của lớp (class invariant):</b> {@code getBalance() >= getMinimumBalance()}.
 *       Tài khoản thông thường có số dư tối thiểu là 0, nên bất biến cơ bản là {@code balance >= 0}.
 *       Lớp con có thể nâng mức tối thiểu, ví dụ {@link SavingAccount} giữ tối thiểu 50.000 VND.</li>
 *   <li><b>Tiền điều kiện</b> được kiểm tra tường minh; vi phạm sẽ ném ngoại lệ checked.</li>
 *   <li><b>Hậu điều kiện</b> được kiểm tra bằng {@code assert} (bật bằng tham số JVM {@code -ea}).</li>
 * </ul>
 *
 * <p>Số dư không có setter public: số dư chỉ thay đổi qua {@link #deposit(double)} và
 * {@link #withdraw(double)}, nhờ vậy bất biến không thể bị phá từ bên ngoài.</p>
 */
public abstract class BankAccount {

    private final String accountNumber;
    private String holderName;
    private double balance;

    /**
     * Khởi tạo tài khoản.
     *
     * @param accountNumber  số tài khoản, không được rỗng
     * @param holderName     tên chủ tài khoản, không được rỗng
     * @param initialBalance số dư ban đầu, phải {@code >= 0}
     * @throws IllegalArgumentException nếu vi phạm một trong các điều kiện trên
     */
    protected BankAccount(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = requireText(accountNumber, "So tai khoan");
        this.holderName = requireText(holderName, "Ten chu tai khoan");
        if (!Double.isFinite(initialBalance) || initialBalance < 0) {
            throw new IllegalArgumentException("So du ban dau phai lon hon hoac bang 0");
        }
        this.balance = initialBalance;
    }

    // ===================== Getter / Setter =====================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    /**
     * @throws IllegalArgumentException nếu tên rỗng
     */
    public void setHolderName(String holderName) {
        this.holderName = requireText(holderName, "Ten chu tai khoan");
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Số dư tối thiểu tài khoản phải luôn giữ lại. Tài khoản thông thường là 0.
     * Lớp con ghi đè để nâng mức tối thiểu (bất biến mở rộng).
     */
    public double getMinimumBalance() {
        return 0;
    }

    // ===================== Nghiệp vụ =====================

    /**
     * Nạp tiền vào tài khoản.
     *
     * @param amount Số tiền cần nạp
     * @throws InvalidAmountException Tiền điều kiện: amount &lt;= 0
     * @post-condition balance_new == balance_old + amount
     * @invariant balance_new &gt;= getMinimumBalance()
     */
    public void deposit(double amount) throws InvalidAmountException {
        requireValidAmount(amount);                                                  // Tiền điều kiện
        double oldBalance = balance;
        balance = oldBalance + amount;
        assert balance == oldBalance + amount : "Hau dieu kien nap tien bi vi pham";  // Hậu điều kiện
        assert invariantHolds() : "Bat bien so du bi vi pham";                         // Bất biến
    }

    /**
     * Rút tiền khỏi tài khoản ngân hàng.
     *
     * @param amount Số tiền cần rút
     * @throws InvalidAmountException Tiền điều kiện: amount &lt;= 0
     * @throws InsufficientBalanceException Tiền điều kiện: balance - amount &lt; getMinimumBalance()
     * @post-condition balance_new == balance_old - amount
     * @invariant balance_new &gt;= getMinimumBalance()
     */
    public abstract void withdraw(double amount)
            throws InsufficientBalanceException, InvalidAmountException;

    // ===================== Hỗ trợ cho lớp con =====================

    /**
     * Kiểm tra tiền điều kiện chung về số tiền giao dịch: phải là số hữu hạn và lớn hơn 0.
     *
     * @throws InvalidAmountException nếu amount &lt;= 0 hoặc không phải số hữu hạn
     */
    protected static void requireValidAmount(double amount) throws InvalidAmountException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("So tien phai lon hon 0 (nhan duoc: " + amount + ")");
        }
    }

    /**
     * Trừ số dư. Lớp con chỉ gọi sau khi đã kiểm tra xong tiền điều kiện của {@code withdraw}.
     *
     * <p>Đây là chốt chặn cuối cho bất biến: nếu một lớp con quên kiểm tra số dư,
     * phương thức ném {@link IllegalStateException} và <b>không</b> thay đổi số dư.</p>
     */
    protected final void debit(double amount) {
        double oldBalance = balance;
        double newBalance = oldBalance - amount;
        if (newBalance < getMinimumBalance()) {
            throw new IllegalStateException("Vi pham bat bien: so du " + newBalance
                    + " se nho hon muc toi thieu " + getMinimumBalance());
        }
        balance = newBalance;
        assert balance == oldBalance - amount : "Hau dieu kien rut tien bi vi pham";
        assert invariantHolds() : "Bat bien so du bi vi pham";
    }

    /** Bất biến của lớp: số dư không nhỏ hơn mức tối thiểu. */
    private boolean invariantHolds() {
        return balance >= getMinimumBalance();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " khong duoc de trong");
        }
        return value.trim();
    }

    // ===================== equals / hashCode / toString =====================

    /** Hai tài khoản là một khi cùng số tài khoản. */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccount other)) {
            return false;
        }
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public final int hashCode() {
        return accountNumber.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s, so du %,.0f VND]",
                getClass().getSimpleName(), accountNumber, holderName, balance);
    }
}
