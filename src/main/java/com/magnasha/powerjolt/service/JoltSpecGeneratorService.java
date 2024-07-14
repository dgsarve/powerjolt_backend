package com.magnasha.powerjolt.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JoltSpecGeneratorService {

    public String generateJoltSpec(String inputJsonString, String outputJsonString) {
        JSONObject inputJson = new JSONObject(inputJsonString);
        JSONObject outputJson = new JSONObject(outputJsonString);
        JSONArray joltSpec = processJoltSpec(inputJson, outputJson);

        // Convert JSONArray to formatted JSON string
        return joltSpec.toString(2);
    }

    public JSONArray processJoltSpec(JSONObject inputJson, JSONObject outputJson) {
        JSONArray spec = new JSONArray();
        generateJoltSpecRecursive("", inputJson, outputJson, spec);
        return spec;
    }

    private void generateJoltSpecRecursive(String path, JSONObject inputJson, JSONObject outputJson, JSONArray spec) {
        // Iterate through keys in inputJson
        for (String key : inputJson.keySet()) {
            Object inputValue = inputJson.get(key);
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (outputJson.has(key)) {
                Object outputValue = outputJson.get(key);
                if (inputValue instanceof JSONObject && outputValue instanceof JSONObject) {
                    // If both are JSONObjects, recursively traverse
                    generateJoltSpecRecursive(currentPath, (JSONObject) inputValue, (JSONObject) outputValue, spec);
                } else {
                    // Add a shift operation for leaf nodes
                    JSONObject shiftOperation = new JSONObject();
                    shiftOperation.put("operation", "shift");
                    JSONObject specObj = new JSONObject();
                    specObj.put(currentPath, currentPath); // Mapping
                    shiftOperation.put("spec", specObj);
                    spec.put(shiftOperation);
                }
            } else {
                // Handle case where key exists in input but not in output
                // Add a default shift operation to include the key
                JSONObject shiftOperation = new JSONObject();
                shiftOperation.put("operation", "shift");
                JSONObject specObj = new JSONObject();
                specObj.put(currentPath, currentPath); // Mapping
                shiftOperation.put("spec", specObj);
                spec.put(shiftOperation);
            }
        }

        // Iterate through keys in outputJson to ensure all keys are covered
        for (String key : outputJson.keySet()) {
            if (!inputJson.has(key)) {
                // Handle case where key exists in output but not in input
                // This may happen in scenarios where output defines additional fields
                String currentPath = path.isEmpty() ? key : path + "." + key;
                JSONObject shiftOperation = new JSONObject();
                shiftOperation.put("operation", "default");
                JSONObject specObj = new JSONObject();
                specObj.put(currentPath, outputJson.get(key)); // Default value from outputJson
                shiftOperation.put("spec", specObj);
                spec.put(shiftOperation);
            }
        }
    }

    // Example usage in main method or controller
    public static void main(String[] args) {
        String inputJsonString = "{ \"Rating\": 1, \"SecondaryRatings\": { \"Design\": 4, \"Price\": 2, \"RatingDimension3\": 1 } }";
        String outputJsonString = "{ \"rating-primary\": 1, \"rating-Design\": 4, \"rating-Price\": 2, \"rating-RatingDimension3\": 1 }";

        JoltSpecGeneratorService service = new JoltSpecGeneratorService();
        String generatedSpec = service.generateJoltSpec(inputJsonString, outputJsonString);

        System.out.println("Generated Jolt Spec:");
        System.out.println(generatedSpec);
    }
}
