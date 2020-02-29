package com.heima.model.crawler.core.callback;


public interface DelayedCallBack {
    /**
     * 延时调用方法
     *
     * @param time
     * @return
     */
    public Object callBack(long time);

    /**
     * 判断是否存在
     *
     * @return
     */
    public boolean isExist();


    /**
     * 获取每次睡眠时间
     *
     * @return
     */
    public long sleepTime();

    /**
     * 超时时间
     *
     * @return
     */
    public long timeOut();
}
