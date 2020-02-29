package com.heima.utils.threadlocal;


import com.heima.model.admin.pojos.AdUser;

public class AdminThreadLocalUtils {

    private final  static ThreadLocal<AdUser> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     * @param user
     */
    public static void setUser(AdUser user){
        userThreadLocal.set(user);
    }

    /**
     * 获取线程中的用户
     * @return
     */
    public static AdUser getUser( ){
        return userThreadLocal.get();
    }

}
