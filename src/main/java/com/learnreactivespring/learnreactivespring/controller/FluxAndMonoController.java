package com.learnreactivespring.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

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
        System.out.println("Number of Processors: " + Runtime.getRuntime().availableProcessors());
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
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
