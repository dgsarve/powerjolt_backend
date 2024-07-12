package com.magnasha.powerjolt.service;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransformService {

    public String transform(String inputJson, String specJson) throws Exception {
        Object input = JsonUtils.jsonToObject(inputJson);
        List<Object> spec = JsonUtils.jsonToList(specJson);
        Chainr chainr = Chainr.fromSpec(spec);
        Object transformedOutput = chainr.transform(input);
        return JsonUtils.toJsonString(transformedOutput);
    }
}
