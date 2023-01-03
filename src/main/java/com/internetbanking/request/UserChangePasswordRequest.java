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
public class UserChangePasswordRequest {

    @NotBlank(message = "Mật khẩu cũ không được bỏ trống")
    @Size(min = 6, max = 50, message = "Độ dài mật khẩu từ {min} - {max}")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống")
    @Size(min = 6, max = 50, message = "Độ dài mật khẩu từ {min} - {max}")
    private String newPassword;
}
