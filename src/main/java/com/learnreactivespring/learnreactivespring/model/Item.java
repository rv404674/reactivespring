package com.learnreactivespring.learnreactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
//  item for retail industry
// Data will give you getter and setter for Item object.
public class Item {

    @Id
    private String id;
    private String description;
    private Double price;
}
