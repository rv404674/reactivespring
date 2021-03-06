package com.learnreactivespring.learnreactivespring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    private static final Logger logger = LoggerFactory.getLogger(FluxAndMonoController.class);

    @GetMapping("/infloop")
    public void returnInf(){
        while(true){
            int x=10;
        }
    }

    @GetMapping("/flux")
    public Flux<Integer> returnFlux(){
        // NOTE:
        // flux doesn't publishes element without a subscribeon.
        // here browser is working as a subscriber. When we are hitting this url
        // as browser is a blocking client, it can't differiante between flux and json response
        // it simply keeps on reloading till 4*4 = 16s, as it expects a json.

        // Returns the number of processors available to the JVM. - 12
        // NOTE: the number of physical cores is this laptop is 6
        // but due to hyper threading,  we have 12 virtual cores.


        Scheduler s= Schedulers.boundedElastic();

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> 10 + i)
                .publishOn(s)
                .map(i -> "value " + i);

        new Thread(() -> flux.subscribe(System.out::println));

        System.out.println("Number of Processors: " + Runtime.getRuntime().availableProcessors());
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(2))
                .log();

    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnStreamFlux(){
        // NOTE:
        // now we will see the flux changes as we are telling the browser to expect a stream.
        // NOTE: infinite flux
        // use case server side events/ stock
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    // Test Mono
    @GetMapping("/mono")
    public Mono<Integer> returnMono(){
        return Mono.just(1)
                .log();
    }

    @GetMapping("/hello")
    public String homePage(){
        return "Hellow World";
    }
}
