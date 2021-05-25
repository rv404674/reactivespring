package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    private static final Logger logger = LoggerFactory.getLogger(FluxAndMonoTest.class);

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
        logger.info("*** Inside the fluxTest Method");
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

    @Test
    public void fluxTestElementsWithoutError(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();
        // to check each element in the flux, use StepVerifier.

        // Verify Complete is what will make stringFlux start emiting elements.
        // verifyComplete ~= Subscribe
        logger.info("*** Inside the fluxTestElementsWithoutError method");
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestElementsWithError(){

        logger.info("*** Inside the fluxTestElementsWithError ");
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                //.expectError(RuntimeException.class)
                .verify();
                //verifyComplete();

        // NOTE:
        // if we do verifyComplete() in the end, we will get thi
        //  expectation "expectComplete" failed (expected: onComplete(); actual: onError
        // Basically, we will be recieveing onError() because of the exception, where as we are asserting with
        // verifyComplete() which expects an onComplete(). Hence change it.
    }

    /**
     * Assert on the number of elements
     */
    @Test
    public void fluxTestElementsCountWithError(){
        logger.info("*** Inside the fluxTestElementsCountWithError");
        Flux <String> stringFlux = Flux.just("Spring", "SpringBoot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    /**
     * Mono Test
     */
    @Test
    public void monoTest(){
        Mono<String> stringMono = Mono.just("Rahul");

        // we are first expecting "Rahul" and then an on complete event.
        StepVerifier.create(stringMono.log())
                .expectNext("Rahul")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error(){
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
