package com.zero9platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    /**
     * 스케줄러 스레드풀 설정
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 동시에 실행 가능한 스케줄러 스레드풀
        // 현재 GPP 3개 + PP 2개 + 여분 1개(대기-지연 방지)
        scheduler.setPoolSize(6);

        // 스레드명 앞문장 프리픽스 - 로그 사용시 구분용
        scheduler.setThreadNamePrefix("zero9-scheduler-");

        // 서버 종료 후, Scheduler shutdown 최대 대기 시간
        scheduler.setAwaitTerminationSeconds(30);

        // 서버 종료 시, 현재 스레드의 작업 완료 대기
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        scheduler.initialize();
        return scheduler;
    }

}
