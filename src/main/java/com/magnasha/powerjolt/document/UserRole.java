package com.magnasha.powerjolt.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("Roles")
public class UserRole {

    @Id
    private long id;
    private String name;

}