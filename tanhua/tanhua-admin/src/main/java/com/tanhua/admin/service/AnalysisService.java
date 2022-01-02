package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.itheima.model.pojo.Analysis;
import com.itheima.model.vo.AnalysisSummaryVo;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AnalysisService {

    @Resource
    private LogMapper logMapper;

    @Resource
    private AnalysisMapper analysisMapper;

    public void analysis() {
        //统计 tb_log  把得到的数据放到 分析统计表

        //2. 设置统计结果
        String format = "yyyy-MM-dd";

        //今天的日期 2019-08-26
        String today = DateUtil.format(new Date(), format);
        //昨天的日期 2019-08-25
        String yestoday = DateUtil.format(DateUtil.yesterday(), format);

        //每日的 新注册用户数
        int countRegistered = logMapper.countByTypeAndDate("0102", today);

        //每日的 活跃用户数
        int countActive = logMapper.countActive(today);

        //每日的 用户登陆次数
        int countLogin = logMapper.countByTypeAndDate("0101", today);

        //每日的 次日留存用户数    昨天注册，今天是活跃用户的
        int countRetention1d = logMapper.countRetention1d(today, yestoday);

        //3 查询已有的统计数据
        Analysis analysis = analysisMapper.selectByRecordDate(today);

        //4 保存或修改数据
        if (analysis == null) {
            analysis = new Analysis();
            analysis.setRecordDate(DateUtil.parse(today, format));
            analysis.setNumLogin(countLogin);
            analysis.setNumActive(countActive);
            analysis.setNumRegistered(countRegistered);
            analysis.setNumRetention1d(countRetention1d);

            analysisMapper.insert(analysis);
        } else {
            analysis.setNumLogin(countLogin);
            analysis.setNumActive(countActive);
            analysis.setNumRegistered(countRegistered);
            analysis.setNumRetention1d(countRetention1d);

            analysisMapper.updateById(analysis);
        }

    }

    //概要统计信息
    public AnalysisSummaryVo getSummary() {
        //1. 创建vo对象
        AnalysisSummaryVo vo = new AnalysisSummaryVo();

        //2. 封装数据
        String format = "yyyy-MM-dd"; //日期格式
        //今天的日期 2019-08-26
        String today = DateUtil.format(new Date(), format);
        //昨天的日期 2019-08-25
        String yestoday = DateUtil.format(DateUtil.yesterday(), format);

        //查询今日的统计数据
        Analysis analysisToday = analysisMapper.selectByRecordDate(today);
        Analysis analysisYestoday = analysisMapper.selectByRecordDate(yestoday);


        //累计用户数 cumulativeUsers
        vo.setCumulativeUsers(analysisMapper.cumulativeUsers());

        //今日新增用户数量  newUsersToday
        vo.setNewUsersToday(Long.parseLong(analysisToday.getNumRegistered().toString()));

        //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌 newUsersTodayRate
        vo.setNewUsersTodayRate(rate(analysisToday.getNumRegistered(), analysisYestoday.getNumRegistered()));

        //今日登录次数 loginTimesToday
        vo.setLoginTimesToday(Long.parseLong(analysisToday.getNumLogin().toString()));

        //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌  loginTimesTodayRate
        vo.setLoginTimesTodayRate(rate(analysisToday.getNumLogin(), analysisYestoday.getNumLogin()));

        //今日活跃用户数量  activeUsersToday
        vo.setActiveUsersToday(Long.parseLong(analysisToday.getNumActive().toString()));

        //今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌 activeUsersTodayRate
        vo.setActiveUsersTodayRate(rate(analysisToday.getNumActive(), analysisYestoday.getNumActive()));

        //3. 返回统计的vo对象
        return vo;
    }

    //涨跌率就是   (今日的数量-昨日的数量) / 昨日的数量  * 100%
    public Integer rate(Integer today, Integer yestoday) {
        //判断昨日的数量不能为0
        if (yestoday == 0) {
            //如果昨日的数量为0  例如，昨日注册用户为0  今日的注册用户为10   增长率是 1000%
            return today * 100;
        } else {
            //(今日的数量-昨日的数量) / 昨日的数量  * 100%
            return (today - yestoday) * 100 / yestoday;
        }
    }

    //public static void main(String[] args) {
    //    System.out.println(1 / 3 * 10000);
    //    System.out.println(1 * 10000 / 3);
    //}
}
