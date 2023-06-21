package com.example.shedlock;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AutoRunnableService {

    @Value("${server.port}")
    private int serverPort;

    @Value("${crp.client.autorunnable.query.delay:30000}")
    private final int queryUnprocessedDelay = 3000;

    @Autowired
    LockProvider lockProvider;

    private ScheduledExecutorService executor;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        log.info("application is ready");

        executor = Executors.newScheduledThreadPool(8,
                new CustomizableThreadFactory(
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "autoRunning" + "-")
                )
        );

        executor.scheduleWithFixedDelay(
                () -> {
                    LockingTaskExecutor lockingTaskExecutor = new DefaultLockingTaskExecutor(lockProvider);
                    lockingTaskExecutor.executeWithLock(queryUnprocessed, new LockConfiguration(
                            Instant.now(),
                            "unprocessed",
                            Duration.ofSeconds(30L),
                            Duration.ofSeconds(3L)
                            )
                    );
                },
                queryUnprocessedDelay / 10,
                queryUnprocessedDelay,
                TimeUnit.MILLISECONDS
        );
        log.info("{} is now started", AutoRunnableService.class.getName());
    }

    private final Runnable queryUnprocessed = () -> {
        log.info("Running auto runnable task on port: {}", serverPort);
    };

}
