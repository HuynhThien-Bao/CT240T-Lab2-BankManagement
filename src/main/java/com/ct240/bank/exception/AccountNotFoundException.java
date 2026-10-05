package com.ct240.bank.exception;

/**
 * Ngoại lệ kiểm tra (checked): không tìm thấy tài khoản theo số tài khoản.
 *
 * <p>Mở rộng ngoài đề bài: dùng cho {@code findAccount} và {@code transferMoney}
 * thay vì trả về {@code null}, để nơi gọi bắt buộc phải xử lý trường hợp không tìm thấy.</p>
 */
public class AccountNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String accountNumber) {
        super("Khong tim thay tai khoan: " + accountNumber);
    }
}
