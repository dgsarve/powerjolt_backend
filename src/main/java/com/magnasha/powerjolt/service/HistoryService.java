package com.magnasha.powerjolt.service;
import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.repository.TransformationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class HistoryService {

    @Autowired
    private TransformationHistoryRepository historyRepository;

    public Flux<TransformationHistory> getHistoryByUserId(String userId) {
        return historyRepository.findByUserId(userId);
    }

    public Mono<TransformationHistory> saveHistory(TransformationHistory history) {
        return historyRepository.save(history);
    }
}
