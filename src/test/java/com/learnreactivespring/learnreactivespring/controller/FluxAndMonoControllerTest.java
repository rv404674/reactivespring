package com.learnreactivespring.learnreactivespring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@WebFluxTest
@AutoConfigureWebTestClient(timeout = "36000")
// NOTE: WebFluxTest only test classes anotated with @RestController
// or Controller. It wont test @ComponentClasses
public class FluxAndMonoControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(FluxAndMonoControllerTest.class);


    @Autowired
    WebTestClient webTestClient;

    @Test
    public void fluxApproach1(){
        logger.info("** In Flux_approach1 Test **");
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // NOTE: exchange will actually hit the endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();

    }

    @Test
    public void fluxApproach2() {
        // Here will be just asserting on the number

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }


}
