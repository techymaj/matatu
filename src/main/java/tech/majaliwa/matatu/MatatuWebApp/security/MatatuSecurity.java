package tech.majaliwa.matatu.MatatuWebApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class MatatuSecurity {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager
                .setUsersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?");
        jdbcUserDetailsManager
                .setAuthoritiesByUsernameQuery("SELECT username, authority FROM roles WHERE username=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                configurer -> configurer
                        .requestMatchers(HttpMethod.GET, "/").hasRole("PLAYER")
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
        ).formLogin(form -> form
                .loginPage("/showCustomLoginForm")
                .loginProcessingUrl("/authenticateTheUser")
                .permitAll()
        ).logout(LogoutConfigurer::permitAll); // add logout support for free

        httpSecurity.httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
