package com.learnreactivespring.learnreactivespring.ItemReactiveRepositortest.java;

import com.learnreactivespring.learnreactivespring.model.Item;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
// TODO: these test cases will run on embedded mongo, not your actual mongo.
// also, keep the mongo on your off, when running this test. Ports will collide.
// though error shown will be something else.
public class ItemReactiveRepositorytest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    // id will be automatically created hence we dont need to pass id
    List<Item> items = Arrays.asList(new Item(null, "Samsung TV", 5000.0),
            new Item(null, "LG TV", 10000.0),
            new Item(null, "Fridge", 20000.0),
            new Item("ABC", "Bose headphones", 50000.0)
            );

    @Before
    public void setUp(){
        // insert some test data before testing.
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save) // s -> itemReactiveRepository.save(s)
                .doOnNext(System.out::println) // s -> System.out.println("inserted item is:" + s)
                .blockLast();
        // TODO: blockLast is done, so that all items are inserted before we run our test.

    }

    @Test
    public void getAll(){
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item) -> item.getDescription().equals("Bose headphones"))
                .verifyComplete();

    }
}
