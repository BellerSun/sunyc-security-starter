package cn.sunyc.security.configuration.yml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;

/**
 * 该框架的配置类
 */
@SuppressWarnings("unused")
@Data
@ConfigurationProperties("sunyc.sso")
public class SunycConfig {
    private Security security = new Security();
    private SSO sso = new SSO();
    public SunycConfig() { }
    public SunycConfig(Security security, SSO sso) {
        this.security = security;
        this.sso = sso;
    }


    /**
     * shiro安全相关的配置
     */
    @Data
    public static class Security{
        /**
         * 登录页面的url，当登录状态验证失败时，会跳转到此链接
         */
        String loginUrl = "login.html";
        /**
         * 角色验证不通过的url，当角色和权限验证失败时，会跳转到此链接
         */
        String unauthorizedUrl = "login.html";

        /**
         * shiro的过滤Map，规则详情见Shiro文档。
         */
        Map<String,String> filterMap = new HashMap<>();

        public Security() {}

        public Security(String loginUrl, String unauthorizedUrl, Map<String, String> filterMap) {
            this.loginUrl = loginUrl;
            this.unauthorizedUrl = unauthorizedUrl;
            this.filterMap = filterMap;
        }

    }

    /**
     * 单点登录相关的配置
     */
    @Data
    public static class SSO{
        /**
         * 登陆接口  **一般不需要自己来配置**
         */
        String loginUrl = "http://hw.sunyc.cn/ycSSO/user/login";
        /**
         * 注销接口  **一般不需要自己来配置**
         */
        String logoutUrl = "http://hw.sunyc.cn/ycSSO/user/logout";
        /**
         * 检查登录状态的接口  **一般不需要自己来配置**
         */
        String checkUrl = "http://hw.sunyc.cn/ycSSO/web/checkUser";
        /**
         * 当前网站的名称，不必须使用域名。不同的项目，此域相同即可共享用户登录状态。
         */
        String webSite = "www.sunyc.cn";
        /**
         * 当前网站的webToken,比较私密，如果泄露请联系sunyc管理员重置
         */
        String webSiteToken = "wwww";

        public SSO() {
        }

        public SSO(String loginUrl, String logoutUrl, String checkUrl, String webSite, String webSiteToken) {
            this.loginUrl = loginUrl;
            this.logoutUrl = logoutUrl;
            this.checkUrl = checkUrl;
            this.webSite = webSite;
            this.webSiteToken = webSiteToken;
        }
    }


}
