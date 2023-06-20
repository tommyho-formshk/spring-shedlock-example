package com.example.shedlock;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledJob {

    @Value("${server.port}")
    private int serverPort;

    @Scheduled(cron = "*/10 * * * * *") //Every 10 second
    @SchedulerLock(name = "cronJob", lockAtLeastFor = "PT7S", lockAtMostFor = "PT30S")
    public void cronJob() throws InterruptedException {
        log.info("Start cron job on port: {}", serverPort);
    }

}
