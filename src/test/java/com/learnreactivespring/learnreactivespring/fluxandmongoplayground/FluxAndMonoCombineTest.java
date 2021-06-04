package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge(){
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

        // NOTE:
//        18:48:40.343 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
//        18:48:40.361 [main] INFO reactor.Flux.Merge.1 - onSubscribe(FluxFlatMap.FlatMapMain)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - request(unbounded)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(A)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(B)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(C)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(D)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(E)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onNext(F)
//        18:48:40.365 [main] INFO reactor.Flux.Merge.1 - onComplete()
    }

    @Test
    public void combineUsingMerge_withDelay(){
        /**
         * NOTE:
         * If there is a delay in flux1, merge wont wait for flux1 to complete. It will start consuming
         * from flux2.
         * NOTE: Order is not guaranteed. It will be in random order.
         */
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.merge(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();

        // output might be
        // (A,D,B,E,C,F) or (D,A,E,B,C,F) or any random order.
    }
}
