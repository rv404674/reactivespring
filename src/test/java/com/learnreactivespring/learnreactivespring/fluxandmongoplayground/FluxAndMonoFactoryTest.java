package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(FluxAndMonoFactoryTest.class);
    List<String> names = new ArrayList<>(Arrays.asList("Rahul Verma", "Sachin", "Ruby"));

    @Test
    public void fluxUsingIterable(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .log();
        logger.info("*** Inside fluxUsingIterable");

        StepVerifier.create(stringFlux)
                .expectNext("Rahul Verma", "Sachin", "Ruby")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names = new String[]{"Rahul Verma", "Sachin", "Ruby"};
        // Flux.fromStream(names.stream());
        Flux<String> stringFlux = Flux.fromArray(names)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Rahul Verma", "Sachin", "Ruby")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustorEmpty(){
        // NOTE: an empty mono. We will just receive on complete.
        logger.info("*** Inside monoUsingJustorEmpty");
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

}
