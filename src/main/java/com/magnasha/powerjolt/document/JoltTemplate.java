package com.magnasha.powerjolt.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("JoltTemplate")
public class JoltTemplate {
    @Id
    private String id;
    private String category;
    private String name;
    private String description;
    private String inputJson;
    private String specJson;
    private String outputJson;
    private String tags;
    private LocalDateTime timestamp;


}
