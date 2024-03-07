package com.lion.codecatcherbe.domain.coding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@Document(collection = "database_sequences")
public class DataBaseSequence {
    @Id
    private String id;

    private long seq;
}
