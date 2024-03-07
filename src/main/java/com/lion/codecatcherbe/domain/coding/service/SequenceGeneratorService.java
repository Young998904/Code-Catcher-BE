package com.lion.codecatcherbe.domain.coding.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

import java.util.Objects;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import com.lion.codecatcherbe.domain.coding.model.DataBaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;
    public long generateSequence(String seqName) {

        DataBaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
            new Update().inc("seq",1), options().returnNew(true).upsert(true),
            DataBaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }}
