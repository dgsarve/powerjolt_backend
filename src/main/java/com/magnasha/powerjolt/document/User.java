package com.magnasha.powerjolt.document;

import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class User {
    @Id
    private String id;
    private String email;
    private String name;

}
