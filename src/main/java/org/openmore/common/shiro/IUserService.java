package org.openmore.common.shiro;

/**
 * Created by michaeltang on 2018/3/29.
 */
public interface IUserService <T>{
    T getEntityById(Integer uid);
}
