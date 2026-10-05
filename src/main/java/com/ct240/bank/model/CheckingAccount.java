package com.ct240.bank.model;

import com.ct240.bank.exception.InsufficientBalanceException;
import com.ct240.bank.exception.InvalidAmountException;

/**
 * Tài khoản thanh toán (mở rộng ngoài đề bài).
 *
 * <p>Giữ đúng bất biến cơ bản của {@link BankAccount}: {@code balance >= 0}.
 * Lớp này giúp thấy rõ tính đa hình: cùng một lời gọi {@code withdraw},
 * mỗi loại tài khoản áp quy tắc riêng.</p>
 */
public class CheckingAccount extends BankAccount {

    /**
     * @throws IllegalArgumentException nếu số tài khoản/tên rỗng hoặc số dư ban đầu âm
     */
    public CheckingAccount(String accountNumber, String holderName, double initialBalance) {
        super(accountNumber, holderName, initialBalance);
    }

    /**
     * Rút tiền khỏi tài khoản thanh toán.
     *
     * @param amount Số tiền cần rút
     * @throws InvalidAmountException Tiền điều kiện: amount &lt;= 0
     * @throws InsufficientBalanceException Tiền điều kiện: balance &lt; amount
     * @post-condition balance_new == balance_old - amount
     * @invariant balance_new &gt;= 0
     */
    @Override
    public void withdraw(double amount) throws InsufficientBalanceException, InvalidAmountException {
        requireValidAmount(amount);
        if (getBalance() < amount) {
            throw new InsufficientBalanceException(String.format(
                    "So du khong du. So du hien tai %,.0f VND, can rut %,.0f VND", getBalance(), amount));
        }
        debit(amount);
    }
}
