package com.khangmoihocit.minimart.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // --- Lỗi Chung & Validation (1xxx) ---
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST_DATA(1001, "Dữ liệu không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1002, "Ngày sinh không hợp lệ. Bạn phải đủ {min} tuổi.", HttpStatus.BAD_REQUEST),
    INFO_NOT_BLANK(1003, "Thông tin không được để trống", HttpStatus.BAD_REQUEST),
    ID_UPDATE_NOT_BLANK(1004, "User id cần cập nhật không được trống.", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT(1005, "Lỗi dịnh dạng ngày (yyyy-mm-dd)", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PATH(1006, "Lỗi đường dẫn", HttpStatus.NOT_FOUND),


    // --- Lỗi Người dùng & Xác thực (2xxx) ---
    USER_EXISTED(2001, "Tên đăng nhập đã tồn tại.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2002, "Email này đã được sử dụng.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(2003, "Không tìm thấy người dùng.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(2004, "Mật khẩu phải có ít nhất 8 ký tự.", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(2005, "Tên đăng nhập phải có ít nhất 3 ký tự.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(2006, "Sai tên đăng nhập hoặc mật khẩu.", HttpStatus.BAD_REQUEST),
    ROLE_USER_NOT_EXIST(2007, "Role user chưa được khởi tạo", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(2008, "Người dùng không tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(2009, "Role không tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_ADMIN_NOT_INITIALIZED(2010, "role admin chưa được khởi tạo", HttpStatus.NOT_FOUND),
    EMAIL_INVALID(2011, "Email không đúng định dạng.", HttpStatus.BAD_REQUEST),



    // --- Lỗi Phân quyền (21xx) ---
    // Lỗi này xảy ra khi người dùng cố gắng truy cập tài nguyên cần đăng nhập
    // mà không cung cấp token hợp lệ.
    // HTTP Status là 401.
    UNAUTHENTICATED(2101, "Vui lòng đăng nhập để thực hiện chức năng này.", HttpStatus.UNAUTHORIZED), // 401

    // Lỗi này xảy ra khi người dùng đã đăng nhập (đã xác thực), nhưng không có đủ quyền (role) để truy cập tài nguyên.
    // Ví dụ: USER cố gắng truy cập API của ADMIN.
    // HTTP Status là 403.
    ACCESS_DENIED(2102, "Bạn không có quyền truy cập tài nguyên này.", HttpStatus.FORBIDDEN), // 403,
    ERROR_REFRESH_TOKEN(2103, "Lỗi ở refresh token", HttpStatus.BAD_REQUEST),// 403
    REFRESH_TOKEN_EXPIRED(2104, "quá thời gian refresh token", HttpStatus.BAD_REQUEST),

    // --- Lỗi Sản phẩm & Danh mục (3xxx) ---
    PRODUCT_NOT_FOUND(3001, "Không tìm thấy sản phẩm.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(3002, "Không tìm thấy danh mục.", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(3003, "Sản phẩm đã hết hàng.", HttpStatus.BAD_REQUEST),

    // --- Lỗi Giỏ hàng & Đơn hàng (4xxx) ---
    CART_IS_EMPTY(4001, "Giỏ hàng của bạn đang trống.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(4002, "Không tìm thấy đơn hàng.", HttpStatus.NOT_FOUND),
    CANNOT_CANCEL_ORDER(4003, "Không thể hủy đơn hàng ở trạng thái này.", HttpStatus.BAD_REQUEST),

    // --- Lỗi Mã giảm giá (Coupon) (5xxx) ---
    COUPON_NOT_FOUND(5001, "Mã giảm giá không tồn tại hoặc đã hết hạn.", HttpStatus.NOT_FOUND),
    COUPON_EXPIRED(5002, "Mã giảm giá đã hết hạn.", HttpStatus.BAD_REQUEST),
    COUPON_CONDITIONS_NOT_MET(5003, "Đơn hàng không đủ điều kiện áp dụng mã giảm giá.", HttpStatus.BAD_REQUEST),

    ;


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
