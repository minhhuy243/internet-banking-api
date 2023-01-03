package com.internetbanking.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.internetbanking.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequest {

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Định dạng Email không hợp lệ")
    private String email;

    @NotBlank(message = "Tên không được bỏ trống")
    @Size(min = 10, max = 50, message = "Độ dài tên từ {min} - {max}")
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.BIRTHDAY_FORMAT)
    private LocalDate birthday;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "(^((?=(0))[0-9]{10})$)", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ không được bỏ trống")
    @Size(min = 10, max = 100, message = "Độ dài địa chỉ từ {min} - {max}")
    private String address;

    private String password;
}
