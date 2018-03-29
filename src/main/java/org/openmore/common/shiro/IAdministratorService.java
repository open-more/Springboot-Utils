package org.openmore.common.shiro;

/**
 * Created by michaeltang on 2018/3/29.
 */
public interface IAdministratorService <T>{
    T getEntityById(Integer uid);
}
