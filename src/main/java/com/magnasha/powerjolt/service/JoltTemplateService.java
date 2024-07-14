package com.magnasha.powerjolt.service;
import com.magnasha.powerjolt.document.JoltTemplate;
import com.magnasha.powerjolt.repository.JoltTemplateRepository;
import com.magnasha.powerjolt.wsdto.JoltTemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class JoltTemplateService {

    private final JoltTemplateRepository repository;

    @Autowired
    public JoltTemplateService(JoltTemplateRepository repository) {
        this.repository = repository;
    }

    public Flux<JoltTemplateResponse> getAllTemplatesGroupedByCategory() {
        return repository.findAll()
                .collectList()
                .flatMapMany(joltTemplates -> Flux.fromIterable(joltTemplates)
                        .collectMultimap(JoltTemplate::getCategory)
                        .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                                .map(entry -> new JoltTemplateResponse(entry.getKey(), (List<JoltTemplate>) entry.getValue()))));
    }
}
