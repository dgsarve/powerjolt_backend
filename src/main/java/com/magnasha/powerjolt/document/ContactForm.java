package com.magnasha.powerjolt.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("contact_form")
public class ContactForm {
    @Id
    private Long id;
    private String name;
    private String query;
    private String email; // Optional: if you want to track the user who submitted the form
}
