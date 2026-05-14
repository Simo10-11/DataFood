package it.unife.sample.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Espongo un bean singleton cosi Spring lo inietta nel service dove facciamo register e login
        return new BCryptPasswordEncoder();
    }
}
