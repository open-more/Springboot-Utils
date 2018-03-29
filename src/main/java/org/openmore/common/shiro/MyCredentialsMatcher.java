package org.openmore.common.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by michaeltang on 2017/7/31.
 */
public class MyCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken,
                                      AuthenticationInfo info) {
        // 在校验时已经验证过
        return true;
    }
}
