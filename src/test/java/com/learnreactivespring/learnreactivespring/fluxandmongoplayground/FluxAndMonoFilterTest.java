package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    List<String> names = new ArrayList<>(Arrays.asList("Rahul", "Ramaaa", "Suresham", "Sachin"));

    /**
     * Filter will basically do what the name suggest. After filtering flux will only pass Rahul and Ramaa.
     * i.e subscriber will only recieve Rahul and Ramaa.
     */
    @Test
    public void fluxTestWithFilter(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("Ra"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Rahul", "Ramaaa")
                .verifyComplete();
    }

    @Test
    public void fluxTestWithCount(){
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 6)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Suresham")
                .verifyComplete();
    }
}
