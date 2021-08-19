package com.learnreactivespring.learnreactivespring.initialize;

import com.learnreactivespring.learnreactivespring.model.Item;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    public List<Item> data(){
        return Arrays.asList(
                new Item(null, "SamSung WashingMachine", 51000.0),
                new Item(null, "Mobile Phone", 15000.0),
                new Item("ABC", "watch", 1000.0)
        );
    }

    public void initialDataSetup(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(s -> itemReactiveRepository.save(s))
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from Command Line Runner: " + item);
                });
    }

}
