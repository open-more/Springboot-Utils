package org.openmore.common.shiro;

import org.openmore.common.utils.*;
import org.openmore.common.exception.*;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by michaeltang on 2017/7/30.
 */
public class MyRealm extends AuthenticatingRealm {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService userService;
    @Autowired
    private IAdministratorService administratorService;


    @Override
    public String getName() {
        return "myRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return super.getCredentialsMatcher();
    }

    /**
     * 用于验证需要授权的信息的合法性，如果信息不合法，则抛出异常
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userToken = (String) authenticationToken.getPrincipal();  //获得手机设备token
        String deviceToken = new String((char[]) authenticationToken.getCredentials()); //得到用户token
        logger.debug("deviceToken = " + deviceToken);
        logger.debug("userToken = " + userToken);
        String userId = null;
        String scope = "app";
        try {
            userId = CommonUtils.getUserIdFromToken(userToken, deviceToken);
            scope = CommonUtils.getScopeFromToken(userToken, deviceToken);
            logger.debug("userId = " + userId);
            logger.debug("scope = " + scope);
        } catch (InvalidTokenException e) {
            throw new IncorrectCredentialsException(); //如果密码错误
        }
        Object user = null;
        try {
            if (scope.equals("app")) {
                user = userService.getEntityById(Integer.valueOf(userId));
            } else if (scope.equals("backend")) {
                user = administratorService.getEntityById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            logger.debug("数据库查询出错:{}", e.getMessage());
            throw new UnknownAccountException();
        }
        if (user == null) {
            logger.debug("userId = {} 的 user不存在", userId);
            throw new UnknownAccountException();
        }
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；保存用户信息，在crendtialMatcher里不做检查
        return new SimpleAuthenticationInfo(user, deviceToken, getName());
    }

//   如果要实现自定授权及权限管理，需要继承：AuthorizingRealm
//    /**
//     * 自定义授权及权限管理
//     * @param principalCollection
//     * @return
//     */
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        return null;
//    }
}
