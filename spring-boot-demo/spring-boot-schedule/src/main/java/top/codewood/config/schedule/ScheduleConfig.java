package top.codewood.config.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduleConfig {

    static final Logger LOGGER = LoggerFactory.getLogger(ScheduleConfig.class);

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(6);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("TaskSchedulerThreadPool-");
        return taskScheduler;
    }

    @Async
    //@Scheduled(cron = "2/5 * * * * ?")
    public void second5Step() {
        LOGGER.info("second5Step, thread: {}, time: {}", Thread.currentThread().getName(), LocalDateTime.now());
    }

    //@Scheduled(cron = "0 0/1 * * * ?")
    public void minite1Step() {
        LOGGER.info("minite1Step, thread: {}, time: {}", Thread.currentThread().getName(), LocalDateTime.now());
    }

    //@Scheduled(fixedRate = 5000)
    public void fixedRate5Step() {
        LOGGER.info("fixedRate5Step, thread: {}, time: {}", Thread.currentThread().getName(), LocalDateTime.now());
    }

}
