package com.ssafy.goose.domain.factcheck.repository;

import com.ssafy.goose.domain.factcheck.model.FactCheck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactCheckRepository extends MongoRepository<FactCheck, String> {
}
