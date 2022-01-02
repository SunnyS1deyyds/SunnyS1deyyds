package com.itheima.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisSummaryVo {
    /**
     * 累计用户数
     */
    private Long cumulativeUsers;
    /**
     * 过去30天活跃用户数
     */
    private Long activePassMonth;
    /**
     * 过去7天活跃用户
     */
    private Long activePassWeek;
    /**
     * 今日新增用户数量
     */
    private Long newUsersToday;
    /**
     * 今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer newUsersTodayRate;
    /**
     * 今日登录次数
     */
    private Long loginTimesToday;
    /**
     * 今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer loginTimesTodayRate;
    /**
     * 今日活跃用户数量
     */
    private Long activeUsersToday;
    /**
     * 今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private Integer activeUsersTodayRate;
}