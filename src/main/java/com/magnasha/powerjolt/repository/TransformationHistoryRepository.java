package com.magnasha.powerjolt.repository;

import com.magnasha.powerjolt.document.TransformationHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransformationHistoryRepository extends ReactiveMongoRepository<TransformationHistory, String> {
    Flux<TransformationHistory> findByUserId(String userId);
}
