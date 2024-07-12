package com.magnasha.powerjolt.controller.reactive;

import com.magnasha.powerjolt.config.CurrentUser;
import com.magnasha.powerjolt.config.UserPrincipal;
import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.service.HistoryService;
import com.magnasha.powerjolt.service.OpenAiService;
import com.magnasha.powerjolt.service.TransformService;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.utils.JwtUtil;
import com.magnasha.powerjolt.wsdto.TransformRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class JoltTransformerController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private HistoryService historyService;

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
    public Mono<ResponseEntity<String>> transformJson(@RequestBody TransformRequest request, @CurrentUser UserPrincipal userPrincipal) throws Exception {
        String transformedJson = transformService.transform(request.getInputJson(), request.getSpecJson());
        List<String> authorization = null;//serverHttpRequest.getHeaders().get("Authorization");
        if (!authorization.isEmpty()) {
            String username = "";
            TransformationHistory history = new TransformationHistory();
            history.setUserId(username);
            history.setInputJson(request.getInputJson());
            history.setSpecJson(request.getSpecJson());
            history.setOutputJson(transformedJson);
            history.setTimestamp(LocalDateTime.now());
            historyService.saveHistory(history);
        }
        return Mono.just(ResponseEntity.ok(transformedJson));

    }

    @GetMapping("/history")
    public Mono<ResponseEntity<Flux<TransformationHistory>>> getHistory(Principal principal) {

        return Mono.just(ResponseEntity.ok(historyService.getHistoryByUserId("")));
    }


}
