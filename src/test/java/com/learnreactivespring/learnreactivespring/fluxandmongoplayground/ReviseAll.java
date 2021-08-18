package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviseAll {

    @Test
    public void fluxTest(){
        Flux.just("Spring", "SpringBoot", "rahul")
                .map(s -> s.concat("verma"))
                .subscribe(System.out::println);

        Mono.just("Rahul")
                .map(s -> s.concat("verma"))
                .subscribe(System.out::println);

        List<String> names = new ArrayList<>(Arrays.asList("Rahul Verma", "Scahin Verma"));
        Flux<String> flux2 = Flux.fromIterable(names)
                .log();

        // subsribe will basically start the action.
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("RAHUL", "SURESHAMA")
                .verifyComplete();

        // filter will remove those that satisfy, the filter
        Flux<String> stringFlux1 = Flux.fromIterable(names)
                .filter( s -> s.length() > 6)
                .map(String::toLowerCase)
                .repeat(1);

        // map mono ya flux chada ke de deta hai.
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .flatMap(s -> {
                    return Flux.fromIterable(converToList(s));
                })
                .log();
        StepVerifier.create(stringFlux2)
                .expectNextCount(12)
                .verifyComplete();

        Flux<String> flux1 = Flux.just("A","B").delayElements(Duration.ofSeconds(1));
        Flux<String> flux3 = Flux.just("C","D").delayElements(Duration.ofSeconds(1));
        Flux<String> mergedFlux = Flux.merge(flux1, flux3);

        // It will be a random order and flux wont wait for eath other if there is a wait.
        Flux<String> mergeFlux = Flux.zip(flux1, flux2, (t1,t2) -> {
            return t1.concat(t2);
        });



    }

    public List<String> converToList(String s){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return Arrays.asList(s, "newvalue");
    }



}
