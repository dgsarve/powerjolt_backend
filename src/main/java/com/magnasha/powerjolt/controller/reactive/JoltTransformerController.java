package com.magnasha.powerjolt.controller.reactive;

import com.magnasha.powerjolt.populator.TransformationHistoryPopulator;
import com.magnasha.powerjolt.service.OpenAiService;
import com.magnasha.powerjolt.service.TransformService;
import com.magnasha.powerjolt.service.TransformationHistoryService;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.utils.JwtUtil;
import com.magnasha.powerjolt.wsdto.TransformRequest;
import com.magnasha.powerjolt.wsdto.TransformationHistoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class JoltTransformerController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private TransformationHistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransformService transformService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/generate-spec")
    public Mono<Map<String, Object>> generateJoltSpec(@RequestBody Map<String, String> request) {
        String inputJson = request.get("input");
        String outputJson = request.get("output");
        return openAiService.generateSpec(inputJson, outputJson);
    }

    @PostMapping("/transform")
    public Mono<ResponseEntity<String>> transformJson(@RequestBody TransformRequest request) {
        String transformedJson = transformService.transform(request.getInputJson(), request.getSpecJson());
        return userService.getCurrentUser()
                .flatMap(user -> {
                    if (user != null) {
                        String username = user.getEmail();
                        com.magnasha.powerjolt.document.TransformationHistory history = TransformationHistoryPopulator.populate(username, request, transformedJson);
                        return historyService.saveHistory(history)
                                .thenReturn(ResponseEntity.ok(transformedJson));
                    }
                    return Mono.just(ResponseEntity.ok(transformedJson));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.ok(transformedJson)));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/history")
    public Mono<ResponseEntity<Flux<TransformationHistoryResponse>>> getHistory() {
        return userService.getCurrentUser()
                .flatMap(user -> {
                    if (user != null) {
                        String userId = user.getEmail();
                        return Mono.just(ResponseEntity.ok(historyService.getHistoryByUserId(userId)));
                    } else {
                        return Mono.just(ResponseEntity.ok(Flux.empty()));
                    }
                });
    }

}
