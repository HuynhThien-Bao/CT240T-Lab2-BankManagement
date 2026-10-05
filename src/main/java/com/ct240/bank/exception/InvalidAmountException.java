package com.ct240.bank.exception;

/**
 * Ngoại lệ kiểm tra (checked): số tiền giao dịch không hợp lệ (amount &lt;= 0).
 *
 * <p>Được ném ra khi vi phạm tiền điều kiện của {@code deposit}, {@code withdraw}
 * và {@code transferMoney}: số tiền phải lớn hơn 0.</p>
 *
 * <p>Là checked exception vì đây là lỗi nghiệp vụ có thể xảy ra trong vận hành bình thường
 * (người dùng nhập sai), nên trình biên dịch buộc nơi gọi phải xử lý.</p>
 */
public class InvalidAmountException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidAmountException(String message) {
        super(message);
    }
}
