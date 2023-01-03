package com.internetbanking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.internetbanking.util.DateUtil;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class User extends JpaEntity {

    @Email(message = "{user.email.format}")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    private String password;

    @NotBlank(message = "Tên không được bỏ trống")
    @Size(min = 3, max = 50)
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.BIRTHDAY_FORMAT)
    private LocalDate birthday;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "(^((?=(0))[0-9]{10})$)", message = "Số điện thoại không đúng định dạng")
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ không được bỏ trống")
    @Size(min = 10, max = 100, message = "{user.address.size}")
    private String address;

    @ManyToOne
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserRefreshToken refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    public void addRefreshToken(UserRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        refreshToken.setUser(this);
    }
}
