# CT240T-Lab2-BankManagement

Bài tập 1 (cá nhân) – Bài thực hành 2, học phần **CT240 – Nguyên lý xây dựng phần mềm**.

Hệ thống quản lý tài khoản và giao dịch ngân hàng, áp dụng OOP nâng cao, Java Collections Framework,
Generics (Wildcards/PECS), xử lý ngoại lệ và Đặc tả kỹ thuật (Design by Contract).

**Sinh viên:** Huỳnh Thiên Bảo – B2405021

## Cấu trúc

```
src/main/java/com/ct240/bank/
├── Main.java                              Menu console, bắt mọi ngoại lệ
├── exception/
│   ├── InvalidAmountException.java        (checked) số tiền <= 0
│   ├── InsufficientBalanceException.java  (checked) không đủ số dư
│   └── AccountNotFoundException.java      (checked) không tìm thấy tài khoản – mở rộng
├── model/
│   ├── BankAccount.java                   Lớp trừu tượng, có đặc tả DbC
│   ├── SavingAccount.java                 Tài khoản tiết kiệm, tối thiểu 50.000 VND
│   └── CheckingAccount.java               Tài khoản thanh toán – mở rộng
└── service/
    └── BankManager.java                   Map<String, BankAccount>, chuyển tiền, PECS
```

## Chạy chương trình

- Yêu cầu JDK 21 trở lên. Project biên dịch với `release 21`.
- IntelliJ IDEA: mở thư mục project, chạy lớp `Main`.
- Bật kiểm tra hậu điều kiện (`assert`): *Run → Edit Configurations → Modify options → Add VM options*, nhập `-ea`.
- Khi khởi động, chương trình tạo sẵn 2 tài khoản mẫu: `TK001` (tiết kiệm, 1.000.000 VND) và `TK002` (thanh toán, 500.000 VND).

## Đặc tả kỹ thuật (Design by Contract)

| Thao tác | Tiền điều kiện | Hậu điều kiện | Ngoại lệ khi vi phạm |
|---|---|---|---|
| `deposit(amount)` | `amount > 0` | `balance_new == balance_old + amount` | `InvalidAmountException` |
| `withdraw(amount)` – `CheckingAccount` | `amount > 0`, `balance >= amount` | `balance_new == balance_old - amount` | `InvalidAmountException`, `InsufficientBalanceException` |
| `withdraw(amount)` – `SavingAccount` | `amount > 0`, `balance - amount >= 50.000` | `balance_new == balance_old - amount` | `InvalidAmountException`, `InsufficientBalanceException` |
| `transferMoney(from, to, amount)` | hai tài khoản tồn tại và khác nhau; `amount > 0`; tài khoản nguồn đủ số dư | tổng số dư hai tài khoản không đổi | `AccountNotFoundException`, `IllegalArgumentException`, `InvalidAmountException`, `InsufficientBalanceException` |

**Bất biến của lớp:** `BankAccount`: `balance >= 0`. `SavingAccount`: `balance >= 50.000 VND`.

Cách thể hiện trong code:

- **Tiền điều kiện:** kiểm tra tường minh, vi phạm thì ném checked exception.
- **Hậu điều kiện:** dùng `assert` (bật bằng `-ea`).
- **Bất biến:** `debit()` chặn mọi thao tác làm số dư thấp hơn mức tối thiểu và không thay đổi số dư khi bị chặn.

## Ghi chú thiết kế

- **Không có setter public cho số dư:** số dư chỉ đổi qua `deposit`/`withdraw`, nên bất biến không thể bị phá từ bên ngoài.
- **Số dư tối thiểu được trừu tượng hóa bằng `getMinimumBalance()`:** tài khoản thường là 0, `SavingAccount` là 50.000.
  Lớp con chỉ đổi giá trị tối thiểu chứ không thêm điều kiện mới vào `withdraw`, nên vẫn đúng nguyên tắc thay thế Liskov.
- **PECS:**
  - `calculateTotalBalance(List<? extends BankAccount>)` chỉ đọc danh sách (producer) nên dùng `extends`, truyền được `List<SavingAccount>`.
  - `collectAccountsOfType(Class<T>, Collection<? super T>)` chỉ ghi vào danh sách (consumer) nên dùng `super`.
- **Checked exception** cho lỗi nghiệp vụ (bắt buộc nơi gọi xử lý); `IllegalArgumentException` cho dữ liệu khởi tạo không hợp lệ.
- **Mở rộng ngoài đề bài:** `CheckingAccount` (để thấy đa hình của `withdraw`) và `AccountNotFoundException` (thay cho trả về `null`).
