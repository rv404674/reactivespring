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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    public void fluxApproach1() {
        logger.info("** In Flux_approach1 Test **");
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // NOTE: exchange will actually hit the endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
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

    @Test
    public void fluxApproach3() {
        // NOTE: Test without step verifier

        List<Integer> expectedOutput = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                // NOTE: expectBodyList will wait till all the elements has been recieved and will then build a list
                //  out of the flux.
                .returnResult();

        assertEquals(expectedOutput, entityExchangeResult.getResponseBody());

    }

    @Test
    public void fluxApproach4() {
        List<Integer> expectedOutput = Arrays.asList(1, 2, 3, 4);

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                // NOTE: expectBodyList will wait till all the elements has been recieved and will then build a list
                //  out of the flux.
                .consumeWith((response) -> {
                    assertEquals(expectedOutput, response.getResponseBody());
                });
    }


    // NOTE: Testing infinite stream here
    @Test
    public void fluxStreamApproach(){
        Flux<Long> longFlux = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        // For a infinite sequence, put thenCancel at the end as well.
        StepVerifier.create(longFlux)
                .expectNext(0L, 1L, 2L)
                .thenCancel()
                .verify();
    }


}
