package com.ct240.bank.exception;

/**
 * Ngoại lệ kiểm tra (checked): số dư không đủ để thực hiện giao dịch.
 *
 * <p>Được ném ra khi giao dịch rút/chuyển tiền sẽ vi phạm tiền điều kiện
 * {@code balance - amount >= số dư tối thiểu}, tức là vi phạm bất biến của tài khoản.</p>
 */
public class InsufficientBalanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
