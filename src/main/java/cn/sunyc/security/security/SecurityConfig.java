package cn.sunyc.security.security;

import cn.sunyc.security.configuration.yml.SunycConfig;
import cn.sunyc.security.utils.ShiroUtil;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

/**
 * Shiro 的配置类.
 * 只有在用户把容器中放了客制化的Realm才会配置本类
 */
@Configuration
@ConditionalOnBean(SunycShiroRealm.class)
public class SecurityConfig {

    /**
     * 从yml配置文件读取配置信息的bean
     */
    @Bean("sunycConfig")
    public SunycConfig sunycConfig() {
        return new SunycConfig();
    }


    /**
     * shiro的工具类的bean
     */
    @Bean("shiroUtil")
    public ShiroUtil shiroUtil() {
        return new ShiroUtil();
    }


    @Bean("securityManager")
    public SecurityManager securityManager(SunycShiroRealm sunycShiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(sunycShiroRealm);
        return securityManager;
    }

    @Bean
    @DependsOn({"securityManager", "sunycConfig"})
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, @Lazy SunycConfig sunycConfig) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl(sunycConfig.getSecurity().getLoginUrl());
        factoryBean.setUnauthorizedUrl(sunycConfig.getSecurity().getUnauthorizedUrl());
        // 这里读配置
        factoryBean.setFilterChainDefinitionMap(sunycConfig.getSecurity().getFilterMap());
        return factoryBean;
    }


    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /*
     * 让thymeleaf支持shiro
     */
    /*@Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }*/

    /*@Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        Properties mapping = new Properties();
        // 用户未登录时候，跳转到登录页面
        mapping.setProperty("org.apache.shiro.authz.UnauthenticatedException", "redirect:login.html");
        simpleMappingExceptionResolver.setExceptionMappings(mapping);
        simpleMappingExceptionResolver.setDefaultErrorView("login.html");
        return simpleMappingExceptionResolver;
    }*/
}
