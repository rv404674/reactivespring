package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
            new Item(null, "gym pants", 500.0),
            new Item("123", "gym shorts", 200.0));

    @Before
    // NOTE: This is will be called at the beginning of each test method of this class.
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
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getById(){
        StepVerifier.create(itemReactiveRepository.findById("123"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("gym shorts"))
                .verifyComplete();
    }

    @Test
    public void getByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Sony Earphones").log("findItemByDescription:"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = new Item(null, "marker", 30.0);
        Mono<Item> itemMono = itemReactiveRepository.save(item);
        StepVerifier.create(itemMono.log("saveItem : "))
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId() != null && item1.getDescription().equals("marker")))
                .verifyComplete();
    }

    @Test
    public void updatePrice(){
        double newPrice = 2500.0;

        Flux<Item> updatedItem = itemReactiveRepository.findByDescription("Sony Earphones")
                // map is used for transformation only.
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Sony Earphones"))
                .verifyComplete();
    }
}
