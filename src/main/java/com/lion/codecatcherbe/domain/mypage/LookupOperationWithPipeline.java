package com.lion.codecatcherbe.domain.mypage;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

@AllArgsConstructor
public class LookupOperationWithPipeline implements AggregationOperation {
    private String from;
    private String localField;
    private String as;
    private String userId; // 추가

    @Override
    public Document toDocument(AggregationOperationContext context) {
        Document lookupDocument = new Document();
        lookupDocument.append("from", from);
        lookupDocument.append("let", new Document("problemId", "$" + localField));
        lookupDocument.append("pipeline", Arrays.asList(
            new Document("$match",
                new Document("$expr",
                    new Document("$and", Arrays.asList(
                        new Document("$eq", Arrays.asList("$problemId", "$$problemId")),
                        new Document("$eq", Arrays.asList("$userId", userId))
                    ))
                )
            )
        ));
        lookupDocument.append("as", as);

        return new Document("$lookup", lookupDocument);
    }
}
