package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

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
        // also it is recommended to use filter before map. Because obviously filter will reduce
        // number of elements hence will reduce the overall complexity.
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>6)
                .map(s -> s.toLowerCase())
                .repeat(1) // it means repeat one time
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("sureshama", "shubham theja", "sureshama", "shubham theja") // because of the repeat
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        /**
         * NOTE: you use flatMap when you have to call a db/external service for every element.
         * basically for async things you use flatMap.
         * NOTE:
         * 1. FlatMap creates a new stream from every upstream event.
         * 2. Subscribe to each newly created stream eagerly.
         * 3. Dynamically merge all the newly created streams as they emit. This is called flatMap.
         */
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, D, E, F
                .flatMap(s -> {
                            return Flux.fromIterable(convertToList(s)); // A -> List[A, newValue], B-> List[B, newValue]
                        }) // convertToList is mimicking an external service/db that returns a flux
                .log();

        // If you see we have 6 different list [A, newValue], [B, newValue] ...
        // but flatMap flattens all of them, basically merge all of them
        // NOTE: it will take 6sec.
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap_usingParallel(){
        /**
         * Doing the above function parrallely - subscribeOn
         */
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux< Flux<String> > ->(A,B), (B,C)
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        // If you see we have 6 different list [A, newValue], [B, newValue] ...
        // but flatMap flattens all of them, basically merge all of them
        // NOTE: it will take 6sec.
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap_maintainingOrderParallel(){
        /**
         * Doing the above function thing inOrder - concatMap, but it still took 6 sec
         * use flatMapSequential - will run parrallely and give result in order (2s)
         */
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux< Flux<String> > ->(A,B), (B,C)
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        // If you see we have 6 different list [A, newValue], [B, newValue] ...
        // but flatMap flattens all of them, basically merge all of them
        // NOTE: it will take 6sec.
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    private List<String> convertToList(String s){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
}
