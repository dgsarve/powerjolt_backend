package com.magnasha.powerjolt.controller;

import com.magnasha.powerjolt.controller.reactive.JoltTransformerController;
import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.service.HistoryService;
import com.magnasha.powerjolt.service.OpenAiService;
import com.magnasha.powerjolt.service.TransformService;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.wsdto.TransformRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(JoltTransformerController.class)
public class JoltTransformerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OpenAiService openAiService;

    @MockBean
    private HistoryService historyService;

    @MockBean
    private UserService userService;

    @MockBean
    private TransformService transformService;

    @Test
    @WithMockUser
    public void testGenerateJoltSpec() {
        Map<String, String> request = new HashMap<>();
        request.put("input", "{\"name\":\"John\"}");
        request.put("output", "{\"firstName\":\"John\"}");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("spec", "someSpec");

        Mockito.when(openAiService.generateSpec(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(mockResponse));

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockOAuth2Login()).post().uri("/api/generate-spec").body(BodyInserters.fromValue(request)).exchange().expectStatus().isOk().expectBody().jsonPath("$.spec").isEqualTo("someSpec");
    }

    @Test
    @WithMockUser
    public void testTransformJson() throws Exception {
        TransformRequest transformRequest = new TransformRequest();
        transformRequest.setInputJson("{\n" + "  \"ratings\": {\n" + "    \"primary\": 5,\n" + "    \"quality\": 4,\n" + "    \"design\": 5\n" + "  }\n" + "}");
        transformRequest.setSpecJson("[\n" + "  {\n" + "    \"operation\": \"shift\",\n" + "    \"spec\": {\n" + "      \"ratings\": {\n" + "        \"*\": {\n" + "          // #2 means go three levels up the tree (count from 0),\n" + "          //  and ask the \"ratings\" node, how many of it's\n" + "          //  children have been matched.\n" + "          //\n" + "          // This allows us to put the Name and the Value into\n" + "          //  the same object in the Ratings array.\n" + "          \"$\": \"Ratings[#2].Name\",\n" + "          \"@\": \"Ratings[#2].Value\"\n" + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "]\n");

        String transformedJson = "{\n" + "  \"Ratings\" : [ {\n" + "    \"Name\" : \"primary\",\n" + "    \"Value\" : 5\n" + "  }, {\n" + "    \"Name\" : \"quality\",\n" + "    \"Value\" : 4\n" + "  }, {\n" + "    \"Name\" : \"design\",\n" + "    \"Value\" : 5\n" + "  } ]\n" + "}\n";
        TransformationHistory history = new TransformationHistory();
        history.setUserId("12345");
        history.setInputJson(transformRequest.getInputJson());
        history.setSpecJson(transformRequest.getSpecJson());
        history.setOutputJson(transformedJson);
        history.setTimestamp(LocalDateTime.now());

        Mockito.when(transformService.transform(Mockito.anyString(), Mockito.anyString())).thenReturn(transformedJson);
        Mockito.when(historyService.saveHistory(Mockito.any(TransformationHistory.class))).thenReturn(Mono.empty());

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockOAuth2Login())
                .mutateWith(SecurityMockServerConfigurers.csrf()) // CSRF configuration
                .post().uri("/api/transform")
                .body(BodyInserters.fromValue(transformRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(transformedJson); }

    @Test
    @WithMockUser
    public void testGetHistory() {
        TransformationHistory history = new TransformationHistory();
        history.setUserId("12345");
        history.setInputJson("{\"name\":\"John\"}");
        history.setSpecJson("[spec]");
        history.setOutputJson("{\"firstName\":\"John\"}");
        history.setTimestamp(LocalDateTime.now());

        Flux<TransformationHistory> historyFlux = Flux.just(history);

        Mockito.when(historyService.getHistoryByUserId(Mockito.anyString())).thenReturn(historyFlux);

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockOAuth2Login())
                .mutateWith(SecurityMockServerConfigurers.csrf()) // CSRF configuration
                .get().uri("/api/history")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransformationHistory.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<TransformationHistory> histories = response.getResponseBody();
                    assert histories != null;
                    assertEquals(1, histories.size());
                    assertEquals("12345", histories.get(0).getUserId());
                    assertEquals("{\"name\":\"John\"}", histories.get(0).getInputJson());
                    assertEquals("[spec]", histories.get(0).getSpecJson());
                    assertEquals("{\"firstName\":\"John\"}", histories.get(0).getOutputJson());
                });
    }
}


