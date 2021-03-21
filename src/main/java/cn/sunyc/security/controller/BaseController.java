package cn.sunyc.security.controller;


import cn.sunyc.security.common.CMStr;
import cn.sunyc.security.configuration.yml.SunycConfig;
import cn.sunyc.security.sso.SSOUser;
import cn.sunyc.security.sso.SSOUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SuppressWarnings("unused")
public class BaseController {
    @Autowired
    @Lazy
    SunycConfig sunycConfig;
    @Autowired
    @Lazy
    SSOUtil ssoUtil;

    /**
     * 登陆
     * @param user 要登录的用户
     * @return 登陆是否成功
     */
    @RequestMapping(value = "/doLogin")
    @ResponseBody
    public boolean doLogin(SSOUser user) {
        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), user.getUserPwd());
        // 执行认证登陆
        try {
            subject.login(token);
        } catch (Exception e) {
            return false;// 登录异常，返回登录页面
        }
        // 登陆验证失败。
        if (!subject.isAuthenticated()) {
            token.clear();
            return false;
        }
        // 登陆成功 顺便设置超时时间 120分钟
        SecurityUtils.getSubject().getSession().setTimeout(1000 * 60 * 120);
        Object ssoToken = SecurityUtils.getSubject().getSession().getAttribute(CMStr.SSO_TOKEN);
        //System.out.println("ssoToken:" + ssoToken);
        return true;
    }

    /**
     * 当前登录用户查看一下自己的Token
     */
    @RequestMapping(value = "/seeToken")
    @ResponseBody
    public String seeToken() {
        return (String) SecurityUtils.getSubject().getSession().getAttribute(CMStr.SSO_TOKEN);
    }

    /**
     * 检查某个token是否已经登录
     * @return 用户是否已经登录
     */
    @RequestMapping(value = "/doCheck")
    @ResponseBody
    public boolean doCheck(String userToken) {
        return ssoUtil.checkToken(userToken) != null;
    }


    /**
     * 退出登录当前用户
     */
    @RequestMapping(value = "/doLogout")
    public String doLogout() {
        // 首先去sso服务器退出登录
        String ssoToken = (String) SecurityUtils.getSubject().getSession().getAttribute(CMStr.SSO_TOKEN);
        ssoUtil.logOutSSOUser(ssoToken);
        // 然后在我们本地退出登录
        SecurityUtils.getSubject().logout();
        return "redirect:"+sunycConfig.getSecurity().getLoginUrl();
    }

}
