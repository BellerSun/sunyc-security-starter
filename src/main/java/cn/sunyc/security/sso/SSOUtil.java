package cn.sunyc.security.sso;

import cn.sunyc.security.configuration.yml.SunycConfig;
import cn.sunyc.security.utils.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Data
@Component
public class SSOUtil {
    @Autowired
    @Lazy
    private SunycConfig sunycConfig;

    public String loginSSOUser(String userName, String userPwd) {
        return loginSSOUser(new SSOUser(null, userName, userPwd, null, null));
    }

    /**
     * 向SSO服务器发送登陆请求
     *
     * @return 登录返回的token，登陆失败返回空字符串
     */
    public String loginSSOUser(SSOUser ssoUser) {
        String ssoWebSite = sunycConfig.getSso().getWebSite();
        String ssoLoginUrl = sunycConfig.getSso().getLoginUrl();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("WebSite", ssoWebSite);
        paraMap.put("userName", ssoUser.getUserName());
        paraMap.put("userPwd", ssoUser.getUserPwd());
        try {
            return HttpUtil.get(ssoLoginUrl, paraMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 向SSO服务器发送注销请求
     *
     * @return 返回注销结果，没什么卵用的返回值
     */
    public String logOutSSOUser(String userToken) {
        String ssoLogoutUrl = sunycConfig.getSso().getLogoutUrl();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("userToken", userToken);
        try {
            return HttpUtil.get(ssoLogoutUrl, paraMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 向SSO服务器请求，某个Token的用户是不是已经登陆
     *
     * @param userToken 用户发送过来的Token
     * @return 登陆成功：用户的身份信息（在本网站登陆的信息）</p>
     * 如果用户未登录，返回null
     */
    public SSOUser checkToken(String userToken) {
        // server带着webToken和用户信息向SSO发起请求。
        String ssoWebSiteToken = sunycConfig.getSso().getWebSiteToken();
        String ssoCheckUrl = sunycConfig.getSso().getCheckUrl();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("webSiteToken", ssoWebSiteToken);
        paraMap.put("userToken", userToken);
        SSOUser resUser;
        try {
            String res = HttpUtil.post(ssoCheckUrl, paraMap);
            resUser = JSONObject.parseObject(res, SSOUser.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //把SSO服务器的检测结果转换成对象
        return resUser;
    }
}
