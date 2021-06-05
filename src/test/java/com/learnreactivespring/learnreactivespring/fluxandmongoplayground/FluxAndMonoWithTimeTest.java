package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log(); // starts from 0, ....;

        infiniteFlux
                .subscribe((element) -> System.out.println("Value is: "+ element));

        // NOTE: As we are dealing with async and non-blocking prog
        // see the logs. The actual value printing is happening in the || thread.
        // if we dont put sleep, the main thread will exit way before the || thread can print anything.
        Thread.sleep(3000);

        // logs
//        17:46:41.079 [main] INFO reactor.Flux.Interval.1 - request(unbounded)
//        17:46:41.185 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(0)
//        Value is: 0
//        17:46:41.280 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(1)
//        Value is: 1
    }

    @Test
    public void infiniteTest(){
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteTest_withDelay(){
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }


}
