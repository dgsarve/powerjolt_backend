package com.magnasha.powerjolt.populator;

import com.magnasha.powerjolt.document.TransformationHistory;
import com.magnasha.powerjolt.wsdto.TransformRequest;

import java.time.LocalDateTime;

public class TransformationHistoryPopulator {

    public static TransformationHistory populate(String username, TransformRequest request, String transformedJson) {
        TransformationHistory history = new TransformationHistory();
        history.setUserId(username);
        history.setInputJson(request.getInputJson());
        history.setSpecJson(request.getSpecJson());
        history.setOutputJson(transformedJson);
        history.setTimestamp(LocalDateTime.now());
        return history;
    }
}