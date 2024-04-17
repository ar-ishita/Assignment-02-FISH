package org.assign2.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {



    /*@Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) {
//        httpSecurity.authorizeExchange(e->e.anyExchange().authenticated())
//        httpSecurity.authorizeExchange(e->e.pathMatchers(HttpMethod.GET).permitAll())
        httpSecurity.authorizeExchange(e -> e.pathMatchers("/eureka/**"))
                .authorizeExchange(e -> e.anyExchange().permitAll())
                .oauth2ResourceServer(oAuth2ResourceServerSpec ->
//                        oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));
                        oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
        httpSecurity.csrf(csrfSpec -> csrfSpec.disable());
        return httpSecurity.build();
    }*/

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/eureka/**").permitAll()  // Assuming you want to permit all under "/eureka/**"
                        .anyExchange().authenticated())  // Secures all other requests
                .oauth2ResourceServer(oAuth2ResourceServer ->
                        oAuth2ResourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
                http.csrf(csrfSpec -> csrfSpec.disable());

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
