package com.internetbanking.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.internetbanking.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
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
public class UserRequest {

    @Email(message = "{user.email.format}")
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    private String password;

    @NotBlank(message = "Tên không được bỏ trống")
    @Size(min = 3, max = 50)
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.BIRTHDAY_FORMAT)
    private LocalDate birthday;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "(^((?=(0))[0-9]{10})$)")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ không được bỏ trống")
    @Size(min = 10, max = 100)
    private String address;

    private String roleCode;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống")
    @Size(min = 6, max = 50)
    private String newPassword;
}
