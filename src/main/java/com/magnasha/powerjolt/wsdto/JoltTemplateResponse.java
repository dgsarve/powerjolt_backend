package com.magnasha.powerjolt.wsdto;

import com.magnasha.powerjolt.document.JoltTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@Setter
@Getter
@AllArgsConstructor
public class JoltTemplateResponse {
    private String category;
    private List<JoltTemplate> joltTemplates;

}
