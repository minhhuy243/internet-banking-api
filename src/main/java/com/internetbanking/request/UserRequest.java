package com.internetbanking.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserRequest {

    @Email(message = "{user.email.format}")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "{user.password.not-blank}")
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank(message = "{user.full-name.not-blank}")
    @Size(min = 3, max = 50, message = "{user.full-name.size}")
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.BIRTHDAY_FORMAT)
    private LocalDate birthday;

    @NotBlank(message = "{user.phone-number.not-blank}")
    @Pattern(regexp = "(^((?=(0))[0-9]{10})$)", message = "{user.phone-number.pattern}")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank(message = "{user.address.not-blank}")
    @Size(min = 20, max = 100, message = "{user.address.size}")
    private String address;

//    private Role role;
}
