package com.learnreactivespring.learnreactivespring.fluxandmongoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisher() throws InterruptedException {
        /**
         * NOTE: coldPublisher - emits value from the beginning. If you see subscriber1 and subscriber2 down below
         * even though, subscriber1 has read the values, subscriber 2 will again read from the beginning.
         * It is like a HTTP request, every time a HTTP request is sent, a new HTTP response will be built and sent
         * to the client.
         */
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        // 1
        stringFlux.subscribe(s -> System.out.println("Subscriber 1 value: " + s));
        Thread.sleep(3000);

        // 2
        stringFlux.subscribe(s -> System.out.println("Subscriber 2 value: " + s));
        Thread.sleep(4000);

//        Subscriber 1 value: A
//        Subscriber 1 value: B
//        Subscriber 1 value: C
//        Subscriber 2 value: A
//        Subscriber 1 value: D
//        Subscriber 2 value: B
//        Subscriber 1 value: E
//        Subscriber 2 value: C
//        Subscriber 1 value: F
//        Subscriber 2 value: D

    }

    @Test
    public void hotPublisher() throws InterruptedException{
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        // subscriber 1
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 value: " + s));
        Thread.sleep(3000);

        // subscriber 2
        // NOTE: wont emit values from the begingging.
        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 value: " + s));
        Thread.sleep(4000);

        // NOTE:
        // unlike above, subscriber 2 started reading from C instead of A.

//        Subscriber 1 value: A
//        Subscriber 1 value: B
//        Subscriber 1 value: C
//        Subscriber 2 value: C
//        Subscriber 1 value: D
//        Subscriber 2 value: D
//        Subscriber 1 value: E
//        Subscriber 2 value: E
//        Subscriber 1 value: F
//        Subscriber 2 value: F
    }
}
