package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxErrorHandling {

    @Test
    public void fluxErrorHandling(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .onErrorResume( (e) -> { // NOTE: this blocks gets executed once the error is raised
                    System.out.println("Exception is: " + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify()
        .expectNext("default", "default1")
                .verifyComplete();

        // A,B,C, default, default1
    }

    @Test
    public void fluxErrorHandling_onReturn(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                // NOTE: onErrorReturn will just return a flux
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify()
                .expectNext("default")
                .verifyComplete();

        // A,B,C, default
    }

    @Test
    public void fluxErrorHandling_onErrorMap(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                // NOTE: onErrorMap will map error to classes of your choice
        .onErrorMap((e) -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
//                .expectError(RuntimeException.class)
//                .verify()
                .verify();

        // A,B,C, default
    }


}
