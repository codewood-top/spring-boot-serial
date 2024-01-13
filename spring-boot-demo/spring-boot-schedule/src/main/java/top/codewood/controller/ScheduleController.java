package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("schedule")
public class ScheduleController {

    private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    public final TaskScheduler taskScheduler;

    private final Map<String, ScheduledFuture> taskMap = new HashMap<>();

    public ScheduleController(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @RequestMapping("task/add")
    public String addTask(String taskName, String cron) {
        ScheduledFuture<?> future = taskMap.get(taskName);
        if (future != null) return "exists";
        future = taskScheduler.schedule(() -> {
            logger.info("task: {}, thread: {}, time: {}", taskName, Thread.currentThread().getName(), LocalDateTime.now());
        }, triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        });
        taskMap.put(taskName, future);
        return "success";
    }

    @RequestMapping("task/cancel")
    public String cancelTask(String taskName) {
        ScheduledFuture future = taskMap.get(taskName);
        if (future == null) return "none";
        future.cancel(false);
        return "success";
    }

}
