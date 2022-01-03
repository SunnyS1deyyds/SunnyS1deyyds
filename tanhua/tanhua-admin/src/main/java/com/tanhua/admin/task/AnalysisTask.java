package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AnalysisTask {

    @Autowired
    private AnalysisService analysisService;

    //创建定时任务   每隔5秒打印系统时间
    //                  秒 分 时 日 月 周 年
    //Spring的任务调度，Cron表达式只支持6位，不能设置年，代表每年都会执行
    //如果定时任务需要指定年，需要使用Quartz
    @Scheduled(cron = "0 0 * * * ?")//正常是每隔1小时执行，为了上课的效果改为每隔5秒执行
    public void analysis() {
        System.out.println("定时任务开始了：" + new Date());
        analysisService.analysis();//执行数据统计，并储存
        System.out.println("定时任务结束了：" + new Date());
    }
}
