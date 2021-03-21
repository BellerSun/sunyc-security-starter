package cn.sunyc.security.security;

import cn.sunyc.security.common.CMStr;
import cn.sunyc.security.sso.SSOUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Collection;

/**
 * Shiro 必须的验证和授权的Realm。
 * 这个类给用户预留了三个抽象方法来实现SSO和本系统的用户信息交换。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SunycShiroRealm extends AuthorizingRealm {
    @Autowired
    @Lazy
    private SSOUtil ssoUtil;

    public SunycShiroRealm(){
        setCachingEnabled(false);
    }

    /**
     * 该框架唯一需要自定义的方法。
     * 根据用户的账号来获取他的角色列表。
     *
     * @param account 用户的账号
     * @return 该用户所对应色角色列表
     */
    protected abstract Collection<String> getUserRoles(String account);

    /**
     * 帮助我们基础框架判断系统中当前是不是有这个用户
     * @param account 用户账号
     * @return 有或没有
     */
    protected abstract boolean hasUser(String account);

    /**
     * 当系统中没有这个用户的时候，如何去添加
     * @param account 用户账号
     * @return 添加结果
     */
    protected abstract boolean addUser(String account);


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 从当前登录信息中获取出来当前用户的 账号
        String account = (String) principals.getPrimaryPrincipal();
        // 根据账号获取到他所持有的角色(可以有多个角色)
        // Template Method 设计模式，放到子类中去实现
        authorizationInfo.addRoles(getUserRoles(account));
        return authorizationInfo;
    }

    /**
     * 验证方法，去SSO验证登陆是否成功
     *
     * @throws AccountException 登录失败
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String account = (String) token.getPrincipal();
        String pwd = new String((char[]) token.getCredentials());

        String ssoToken = ssoUtil.loginSSOUser(account, pwd);
        if (ssoUtil.checkToken(ssoToken) == null)
            throw new AccountException("登录失败！");
        // 如果系统中当前没有这个用户，那么就添加
        if (!hasUser(account))
            addUser(account);
        // 把token装进全局，我靠，还真能行
        SecurityUtils.getSubject().getSession().setAttribute(CMStr.SSO_TOKEN,ssoToken);
        return new SimpleAuthenticationInfo(account, pwd, getName());
    }
}
