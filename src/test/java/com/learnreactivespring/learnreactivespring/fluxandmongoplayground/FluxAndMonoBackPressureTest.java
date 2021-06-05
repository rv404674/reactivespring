package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) -> System.out.println("value is " + element),
                (e) -> System.err.println(e), // error handling
                () -> System.out.println("Done"), // after completion
                (subscription -> subscription.request(2)) // subscription
        );

        // NOTE: we wont even get Done, as we are only requesting 2 elements.

    }

    @Test
    public void backPressure_onCancel() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) -> System.out.println("value is " + element),
                (e) -> System.err.println(e), // error handling
                () -> System.out.println("Done"), // after completion
                (subscription -> subscription.cancel()) // subscription
        );
    }

    @Test
    public void customizedBackPressure(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe(
                new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnNext(Integer value) {
                        // NOTE: request for elements one by one, and cancel the flux
                        // once value == 4
                        request(1);
                        System.out.println("Value recieved is:" + value);
                        if (value==4){
                            cancel();
                        }
                    }
                }
        );
    }
}
