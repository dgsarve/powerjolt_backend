package com.magnasha.powerjolt.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public class TransformationHistory {
    @Id
    private String id;
    private String userId;
    private String inputJson;
    private String specJson;
    private String outputJson;
    private LocalDateTime timestamp;


}
