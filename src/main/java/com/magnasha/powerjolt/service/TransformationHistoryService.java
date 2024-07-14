package com.magnasha.powerjolt.service;

import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.repository.TransformationHistoryRepository;
import com.magnasha.powerjolt.wsdto.TransformationHistoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransformationHistoryService {

    @Autowired
    private TransformationHistoryRepository historyRepository;


    public Mono<com.magnasha.powerjolt.document.TransformationHistory> saveHistory(TransformationHistory history) {
        return historyRepository.save(history);
    }

    public Flux<TransformationHistoryResponse> getHistoryByUserId(String userId) {
        return historyRepository.findByUserIdOrderByTimestampDesc(userId)
                .collectList()
                .flatMapMany(this::groupByDate);
    }

    private Flux<TransformationHistoryResponse> groupByDate(List<TransformationHistory> histories) {
        return Flux.fromIterable(
                histories.stream()
                        .collect(Collectors.groupingBy(history -> history.getTimestamp().toLocalDate()))
                        .entrySet()
                        .stream()
                        .map(entry -> new TransformationHistoryResponse(entry.getKey(), entry.getValue()))
                        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                        .collect(Collectors.toList())
        );
    }
}
