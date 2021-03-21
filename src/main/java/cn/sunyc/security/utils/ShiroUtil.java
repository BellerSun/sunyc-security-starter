package cn.sunyc.security.utils;

import cn.sunyc.security.common.CMStr;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    /**
     * 获取当前登录用户的账号
     * @return 当前登录用户的账号
     */
    public String getCurUserAccount(){
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前登录用户的单点登录的token
     * @return 当前登录用户的单点登录的token
     */
    public String getCurUserToken(){
        return (String) SecurityUtils.getSubject().getSession().getAttribute(CMStr.SSO_TOKEN);
    }
}
