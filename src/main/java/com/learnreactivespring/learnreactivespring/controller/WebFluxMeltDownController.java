package com.learnreactivespring.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// NOTE: https://blog.jdriven.com/2020/10/spring-webflux-reactor-meltdown-slow-responses/

@RestController()
@RequestMapping("block")
public class WebFluxMeltDownController {
    @GetMapping(value = "time/{sleepMs}", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> block(@PathVariable long sleepMs) {
        // return Mono.fromSupplier(() -> blockingFunction(sleepMs))
        return Mono.fromSupplier(() -> blockingFunction(sleepMs))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String blockingFunction(long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "OK, wake up from " + sleepMs + " ms sleep";
    }

    @GetMapping(value = "health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String health() {
        return "OK";
    }
}
