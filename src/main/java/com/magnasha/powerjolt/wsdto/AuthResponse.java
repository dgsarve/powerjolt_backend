package com.magnasha.powerjolt.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Setter
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String message;


}