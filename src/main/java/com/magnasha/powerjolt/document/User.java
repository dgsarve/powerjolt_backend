package com.magnasha.powerjolt.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("User")
public class User {
    @Id
    private String id;
    private String email;
    private String name;
    private String picture;
    private boolean subscribed;

}
