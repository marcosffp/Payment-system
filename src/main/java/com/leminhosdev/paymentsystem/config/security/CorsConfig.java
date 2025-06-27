package com.leminhosdev.paymentsystem.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing) para permitir
 * que o backend aceite requisições de outras origens.
 * <p>
 * Neste exemplo, permite chamadas HTTP de qualquer origem para todos os
 * endpoints,
 * com métodos GET, POST, PUT, DELETE e OPTIONS, e qualquer cabeçalho.
 * </p>
 */
@Configuration
public class CorsConfig {

  /**
   * Bean que configura as permissões de CORS para a aplicação.
   *
   * @return WebMvcConfigurer com as regras de CORS definidas.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // Permitir chamadas de qualquer origem
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
      }
    };
  }
}
