package com.ct240.bank.model;

import com.ct240.bank.exception.InsufficientBalanceException;
import com.ct240.bank.exception.InvalidAmountException;

/**
 * Tài khoản tiết kiệm.
 *
 * <p><b>Bất biến mở rộng:</b> {@code balance >= 50.000 VND} (số dư tối thiểu của tài khoản tiết kiệm).</p>
 */
public class SavingAccount extends BankAccount {

    /** Số dư tối thiểu của tài khoản tiết kiệm (VND). */
    public static final double MINIMUM_BALANCE = 50_000;

    /** Lãi suất năm, ví dụ 0.05 nghĩa là 5%/năm. */
    private double interestRate;

    /**
     * @param accountNumber  số tài khoản, không được rỗng
     * @param holderName     tên chủ tài khoản, không được rỗng
     * @param initialBalance số dư ban đầu, phải {@code >= 50.000}
     * @param interestRate   lãi suất năm, phải {@code >= 0}
     * @throws IllegalArgumentException nếu vi phạm một trong các điều kiện trên
     */
    public SavingAccount(String accountNumber, String holderName, double initialBalance, double interestRate) {
        super(accountNumber, holderName, initialBalance);
        if (initialBalance < MINIMUM_BALANCE) {
            throw new IllegalArgumentException(String.format(
                    "So du ban dau cua tai khoan tiet kiem phai tu %,.0f VND", MINIMUM_BALANCE));
        }
        this.interestRate = requireValidRate(interestRate);
    }

    public double getInterestRate() {
        return interestRate;
    }

    /**
     * @throws IllegalArgumentException nếu lãi suất âm
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = requireValidRate(interestRate);
    }

    @Override
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    /**
     * Rút tiền khỏi tài khoản tiết kiệm: không cho phép số dư còn lại nhỏ hơn 50.000 VND.
     *
     * @param amount Số tiền cần rút
     * @throws InvalidAmountException Tiền điều kiện: amount &lt;= 0
     * @throws InsufficientBalanceException Tiền điều kiện: balance - amount &lt; 50.000 VND
     * @post-condition balance_new == balance_old - amount
     * @invariant balance_new &gt;= 50.000 VND
     */
    @Override
    public void withdraw(double amount) throws InsufficientBalanceException, InvalidAmountException {
        requireValidAmount(amount);
        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientBalanceException(String.format(
                    "Tai khoan tiet kiem phai giu toi thieu %,.0f VND. So du hien tai %,.0f VND, "
                            + "chi rut duoc toi da %,.0f VND",
                    MINIMUM_BALANCE, getBalance(), getBalance() - MINIMUM_BALANCE));
        }
        debit(amount);
    }

    private static double requireValidRate(double rate) {
        if (!Double.isFinite(rate) || rate < 0) {
            throw new IllegalArgumentException("Lai suat phai lon hon hoac bang 0");
        }
        return rate;
    }
}
