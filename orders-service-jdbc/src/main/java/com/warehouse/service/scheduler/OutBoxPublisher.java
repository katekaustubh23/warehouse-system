package com.warehouse.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableScheduling
@Slf4j
public class OutBoxPublisher {


    @Scheduled(fixedRate = 10000) // every 10 seconds
    @Transactional
    public void OutBoxPublisher() {

    }
}
