package com.magnasha.powerjolt.controller.reactive;

import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.service.HistoryService;
import com.magnasha.powerjolt.service.OpenAiService;
import com.magnasha.powerjolt.service.TransformService;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.wsdto.TransformRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JoltTransformerController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private HistoryService historyService;


    @Autowired
    private TransformService transformService;


    @PostMapping("/generate-spec")
    public Mono<Map<String, Object>> generateJoltSpec(@RequestBody Map<String, String> request) {
        String inputJson = request.get("input");
        String outputJson = request.get("output");
        return openAiService.generateSpec(inputJson, outputJson);
    }

    @PostMapping("/transform")
    public Mono<ResponseEntity<String>> transformJson(@RequestBody TransformRequest request, Principal principal) throws Exception {

        String transformedJson = transformService.transform(request.getInputJson(), request.getSpecJson());
        String userId = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("id");
        TransformationHistory history = new TransformationHistory();
        history.setUserId(userId);
        history.setInputJson(request.getInputJson());
        history.setSpecJson(request.getSpecJson());
        history.setOutputJson(transformedJson);
        history.setTimestamp(LocalDateTime.now());
        return historyService.saveHistory(history).thenReturn(ResponseEntity.ok(transformedJson));

    }

    @GetMapping("/history")
    public Mono<ResponseEntity<Flux<TransformationHistory>>> getHistory(Principal principal) {
        String userId = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("id");
        return Mono.just(ResponseEntity.ok(historyService.getHistoryByUserId(userId)));
    }
}
