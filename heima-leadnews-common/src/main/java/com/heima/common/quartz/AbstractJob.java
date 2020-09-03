package com.heima.common.quartz;

import com.google.common.collect.Maps;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Map;

/**
 * 所有任务的自动管理抽象类
 */
public abstract class AbstractJob extends QuartzJobBean {

    /**
     * 执行完成后从数据库中删除
     * @return
     */
    public boolean isComplateAfterDelete(){return true;}

    /**
     * 是否启动自动尝试恢复
     * @return
     */
    public boolean isStartAutoRecovery(){return true;}

    /**
     * JOB名称
     * @return
     */
    public String name(){return this.getClass().getName();}

    /**
     * JOB分组
     * @return
     */
    public String group(){return "default";}

    /**
     * JOB描述
     * @return
     */
    public String descJob(){return "";}

    /**
     * Trigger描述
     * @return
     */
    public String descTrigger(){return "";}

    /**
     * 初始化参数
     * @return
     */
    public Map<String,?> initParam(){return Maps.newHashMap();}

    /**
     * 是否自动覆盖
     */
    public boolean isAutoOverwrite(){return true;}

    /**
     * 返回调度策略表达式,可以多个
     * @return
     */
    public abstract String[] triggerCron();

    /**
     * 如果是@DisallowConcurrentExecution，是否继承上次任务执行的结果
     * 该方法未做实现
     * @return
     */
    @Deprecated
    public boolean isExtendPreviouData(){return false;}

}