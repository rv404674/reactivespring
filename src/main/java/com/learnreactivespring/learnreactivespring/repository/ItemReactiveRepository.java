package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item,String> {
}
