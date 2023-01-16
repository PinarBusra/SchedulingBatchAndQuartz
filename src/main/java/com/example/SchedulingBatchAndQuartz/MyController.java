package com.example.SchedulingBatchAndQuartz;

import com.example.SchedulingBatchAndQuartz.config.QuartzConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class MyController {

    private final QuartzConfig cronExpressions;

    @Autowired
    public MyController(QuartzConfig cronExpressions) {
        this.cronExpressions = cronExpressions;
    }

    @GetMapping("/updateCron/cronTaskOne")
    public String updateCronTaskOne(String cron) {
        log.info("new cron :{}", cron);
        cronExpressions.setCronTaskOne(cron);
        return "ok";
    }
    @GetMapping("/updateCron/cronTaskTwo")
    public String updateCronTaskTwo(String cron) {
        log.info("new cron two :{}", cron);
        cronExpressions.setCronTaskTwo(cron);
        return "ok";
    }
}
