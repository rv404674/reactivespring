package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {
    List<String> names = new ArrayList<>(Arrays.asList("Rahul", "Sureshama", "Shubham Theja"));

    @Test
    public void transformUsingMap(){
        /**
         * Map is used to perform transformation
         */
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("RAHUL", "SURESHAMA", "SHUBHAM THEJA")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Count(){
        // NOTE:
        // here s.lenght will return a int. Hence it will be a Flux<Int> not other way
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(5, 9 , 13)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Repeat(){
        // NOTE:
        // if you want the flux to repeat, use repeat
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1) // it means repeat one time
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(5, 9 , 13, 5, 9, 13)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapAndFilter(){
        // NOTE:
        // when you are performing multiple operations on a flux, it is called a pipeline.
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>6)
                .map(s -> s.toLowerCase())
                .repeat(1) // it means repeat one time
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("sureshama", "shubham theja", "sureshama", "shubham theja") // because of the repeat
                .verifyComplete();
    }
}
