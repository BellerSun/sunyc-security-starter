package cn.sunyc.security.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSOUser {
    private Long userId;
    private String userName;
    private String userPwd;
    private String userTel;
    private String userMail;
}
