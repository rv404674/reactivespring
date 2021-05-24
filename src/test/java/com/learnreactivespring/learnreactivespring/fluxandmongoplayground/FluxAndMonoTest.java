package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest(){
        // with concatWith we are attaching an error to the flux.
        // Basically we are throwing Runtime Exception and using concatwith, we are attaching it to the flux.
        // log will print what is happening behind the scenes.
        // NOTE: Once OnError is sent, flux wont sent any more messages.
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("after on error"))
                .log();

        // Now you will start receiving values from flux. Flux is useless if you don't subscribe.
        // :: is lambda
        // "Flux completed successfully will be printed once we recieve the onComplete() event".
        (stringFlux).
                subscribe(System.out::println,
                        (e) -> System.err.println("Exception is" + e),
                        () -> System.out.println("Flux completed successfully"));

        // NOTE: Flux oncomplete logs
//        20:06:35.637 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
//        20:06:35.645 [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
//        20:06:35.646 [main] INFO reactor.Flux.Array.1 - | request(unbounded)
//        20:06:35.646 [main] INFO reactor.Flux.Array.1 - | onNext(Spring)
//        Spring
//        20:06:35.646 [main] INFO reactor.Flux.Array.1 - | onNext(Spring Boot)
//        Spring Boot
//        20:06:35.646 [main] INFO reactor.Flux.Array.1 - | onNext(Reactive Spring)
//        Reactive Spring
//        20:06:35.647 [main] INFO reactor.Flux.Array.1 - | onComplete()

    }
}
