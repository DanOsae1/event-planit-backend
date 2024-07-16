package com.osaebros.eventplanner.config;

import com.osaebros.eventplanner.utils.JwtUtil;
import com.osaebros.eventplanner.utils.KeycloakContainer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@Testcontainers
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=${keycloak.auth-server-url}/realms/${keycloak.realm}",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/certs"
})
public class WebSecurityConfigTest {

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer();

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer();

    @DynamicPropertySource
    static void registerKeycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.auth-server-url", keycloak::getAuthServerUrl);
        registry.add("keycloak.realm", keycloak::getMasterRealm);
        registry.add("keycloak.master.realm", keycloak::getMasterRealm);
        registry.add("keycloak.client_id", keycloak::getClientId);
        registry.add("keycloak-admin.username", keycloak::getUser);
        registry.add("keycloak-admin.password", keycloak::getPass);
//        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloak.getAuthServerUrl() + "/protocol/openid-connect/certs");
//        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
//                () -> keycloak.getAuthServerUrl() + "/realms/master");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Bean
    @Primary
    public OAuth2AuthorizedClientManager authorizedClientManager() {
        return mock(OAuth2AuthorizedClientManager.class);
    }

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @MockBean
    private TokenConverterProperties converterProperties;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("secret"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/v1/registration/**").permitAll()
                        .requestMatchers("/v1/login/**").permitAll()
                        .requestMatchers("/v1/search/**").permitAll()
                        .requestMatchers("/v1/provider/**").permitAll()
                        .anyRequest()
                        .authenticated()
        );
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
        );
//        http.oauth2Login(AbstractHttpConfigurer::disable);
//        http.oauth2Client(AbstractHttpConfigurer::disable);
//        http.oauth2ResourceServer(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            // This is a dummy decoder that always returns a valid JWT
            return new Jwt(token, Instant.now(), Instant.now().plusSeconds(60),
                    Map.of("alg", "none"), Map.of("sub", "test"));
        };
    }
}
