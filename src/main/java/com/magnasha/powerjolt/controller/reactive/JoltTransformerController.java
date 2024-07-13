package com.magnasha.powerjolt.controller.reactive;

import com.magnasha.powerjolt.config.CurrentUser;
import com.magnasha.powerjolt.config.UserPrincipal;
import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.document.User;
import com.magnasha.powerjolt.service.HistoryService;
import com.magnasha.powerjolt.service.OpenAiService;
import com.magnasha.powerjolt.service.TransformService;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.utils.JwtUtil;
import com.magnasha.powerjolt.wsdto.TransformRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

@Slf4j
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
    public Mono<ResponseEntity<String>> transformJson(@RequestBody TransformRequest request) throws Exception {
        // Perform your transformation logic
        String transformedJson = transformService.transform(request.getInputJson(), request.getSpecJson());

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        String username =((User) authentication.getPrincipal()).getEmail();
                        System.out.println("Logged-in user: " + username);

                        // Save the transformation history
                        TransformationHistory history = new TransformationHistory();
                        history.setUserId(username);
                        history.setInputJson(request.getInputJson());
                        history.setSpecJson(request.getSpecJson());
                        history.setOutputJson(transformedJson);
                        history.setTimestamp(LocalDateTime.now());

                        return historyService.saveHistory(history)
                                .thenReturn(ResponseEntity.ok(transformedJson));
                    } else {
                        System.out.println("No authentication information available or not authenticated");
                        return Mono.just(ResponseEntity.ok(transformedJson));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.ok(transformedJson))); // Handle cases where context is empty
    }

    @GetMapping("/history")
    public Mono<ResponseEntity<Flux<TransformationHistory>>> getHistory(Principal principal) {

        return Mono.just(ResponseEntity.ok(historyService.getHistoryByUserId("")));
    }


}
