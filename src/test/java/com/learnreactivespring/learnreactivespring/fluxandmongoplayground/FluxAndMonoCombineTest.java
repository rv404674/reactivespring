package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

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

    @Test
    public void combineUsingConcat_withDelay(){
        /**
         * NOTE: Order is guaranteed. just like string append. "rahul" + "verma" = "rahulverma"
         * concat will wait for flux1 to finish then it will consume flux2.
         * Cons - Slower than Merge.
         */
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        // NOTE: Took 6sec.
        Flux<String> mergeFlux = Flux.concat(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
        // A,B,C,D,E,F
    }

    @Test
    public void combineUsingConcat_withDelay_withVirtualTime(){
        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.concat(flux1, flux2);
        // NOTE: took 0.158s.
        StepVerifier.withVirtualTime(() -> mergeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip_withDelay(){
        /**
         * NOTE: Diff type of merging.
         * Will take one one element of each of the fluxes and then merge it.
         */
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.zip(flux1, flux2, (t1,t2) -> {
            return t1.concat(t2);
        });

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

        // AD, BE, CF
    }


}
