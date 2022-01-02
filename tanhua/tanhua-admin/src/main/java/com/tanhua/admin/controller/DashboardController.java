package com.tanhua.admin.controller;

import com.itheima.model.vo.AnalysisSummaryVo;
import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 概要统计信息
     */
    @GetMapping("/summary")
    public AnalysisSummaryVo getSummary() {
        AnalysisSummaryVo analysisSummaryVo = analysisService.getSummary();
        return analysisSummaryVo;
    }

}