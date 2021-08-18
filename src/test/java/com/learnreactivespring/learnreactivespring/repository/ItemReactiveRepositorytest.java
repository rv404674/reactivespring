package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.model.Item;
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
// datamongo test will just load the classes required to test mongo nothing else.
@RunWith(SpringRunner.class)
public class ItemReactiveRepositorytest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    // id is null, as it is assigned once the document is inserted.
    List<Item> itemList = Arrays.asList(new Item(null, "LG TV", 50000.0),
            new Item(null, "Sony Earphones", 1500.0),
            new Item(null, "gym pants", 500.0));

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(document -> itemReactiveRepository.save(document))
                .doOnNext(( item -> {
                    System.out.println("Inserted Item is: " + item);
                }))
                .log()
                // block till all the operations above are completed.
                .blockLast();
    }


    @Test
    public void getAllItems(){
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
