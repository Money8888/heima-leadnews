package com.heima.common.common.contants;

/**
 * 项目工程通用常量定义类；
 * 如是模块通用，请定义在自模块常量类中；
 */
public class Contants {

    // 项目名称
    public static String APP_NAME = "APP_NAME";
    // 项目字符集编码
    public  final static String CHARTER_NAME = "UTF-8";
    // 当前项目激活的环境
    public static String PROFILE_NAME = "";

    /**
     * 是否是生产环境
     * @return
     */
    public static boolean isProd(){
        return "prod".equalsIgnoreCase(PROFILE_NAME);
    }

}
