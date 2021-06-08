package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime(){
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();

        // NOTE: Time taken 3sec
//        16:17:44.692 [main] INFO reactor.Flux.Take.1 - request(unbounded)
//        16:17:45.695 [parallel-1] INFO reactor.Flux.Take.1 - onNext(0)
//        16:17:46.697 [parallel-1] INFO reactor.Flux.Take.1 - onNext(1)
//        16:17:47.697 [parallel-1] INFO reactor.Flux.Take.1 - onNext(2)
//        16:17:47.698 [parallel-1] INFO reactor.Flux.Take.1 - onComplete()
    }

    @Test
    public void testingWithVirtualTime(){
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3)) // thenAwait is most importantj, without it withVirtualTime wont work.
                .expectNext(0l, 1l, 2l)
                .verifyComplete();

        // all of this is executed in 1s
        // NOTE:
//        16:24:16.016 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
//        16:24:16.078 [main] INFO reactor.Flux.Take.1 - onSubscribe(FluxTake.TakeSubscriber)
//        16:24:16.081 [main] INFO reactor.Flux.Take.1 - request(unbounded)
//        16:24:16.082 [main] INFO reactor.Flux.Take.1 - onNext(0)
//        16:24:16.082 [main] INFO reactor.Flux.Take.1 - onNext(1)
//        16:24:16.082 [main] INFO reactor.Flux.Take.1 - onNext(2)
//        16:24:16.082 [main] INFO reactor.Flux.Take.1 - onComplete()
    }
}
