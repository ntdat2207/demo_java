package com.example.virtual_account.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.parameters.HeaderParameter;

@Configuration
public class SwaggerHeader {
    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                    operation.addParametersItem(new HeaderParameter()
                            .name("merchant_code")
                            .required(true)
                            .description("Merchant Code"));

                    operation.addParametersItem(new HeaderParameter()
                            .name("signature")
                            .required(true)
                            .description(
                                    "Digital Signature. Format: algo:SHA256|ED25519&signature=base64EncodedSignature. Example: algo:SHA256&signature=abc123xyz"));
                }));
    }
}
