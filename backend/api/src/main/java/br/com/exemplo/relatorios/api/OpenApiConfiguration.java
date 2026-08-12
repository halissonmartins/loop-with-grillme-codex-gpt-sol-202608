package br.com.exemplo.relatorios.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {
    @Bean
    OpenAPI relatoriosOpenApi() {
        return new OpenAPI().info(new Info().title("Relatórios agendados").version("0.1.0"));
    }

    @Bean
    OpenApiCustomizer documentaContratoDeErro() {
        return openApi -> components(openApi).addSchemas("Erro", contratoDeErro());
    }

    private static Components components(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        return openApi.getComponents();
    }

    private static ObjectSchema contratoDeErro() {
        ObjectSchema schema = new ObjectSchema();
        schema.setDescription("Resposta de erro da API");
        schema.addProperty("momento", descricao("Momento do erro", new DateTimeSchema()));
        schema.addProperty("descricao", descricao("Descrição do erro", new StringSchema()));
        schema.addProperty("correlationId", descricao("Correlation ID da ocorrência", new StringSchema()));
        schema.addProperty("status", descricao("Status HTTP", new IntegerSchema()));
        schema.addProperty("titulo", descricao("Título do erro", new StringSchema()));
        schema.addProperty("detalhe", descricao("Detalhe do erro", new StringSchema()));
        schema.addProperty("codigoInterno", descricao("Código interno do erro", new StringSchema()));
        schema.addProperty("rota", descricao("Rota da requisição", new StringSchema()));
        schema.setRequired(List.of("momento", "descricao", "correlationId"));
        return schema;
    }

    private static <T extends io.swagger.v3.oas.models.media.Schema<?>> T descricao(String valor, T schema) {
        schema.setDescription(valor);
        return schema;
    }
}
