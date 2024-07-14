package com.magnasha.powerjolt.repository;

import com.magnasha.powerjolt.document.TransformationHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TransformationHistoryRepository extends R2dbcRepository<TransformationHistory, String> {
    Flux<TransformationHistory> findByUserId(String userId);
    Flux<TransformationHistory> findByUserIdOrderByTimestampDesc(String userId);
}

