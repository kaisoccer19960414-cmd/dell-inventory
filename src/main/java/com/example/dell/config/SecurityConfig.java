package com.example.dell.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 開発者本人しか使わない管理画面(/products配下)をBasic認証で保護する。
 * ユーザーは1人だけの想定のため、DBやユーザー登録機能は持たず、
 * 環境変数から読み込んだ1組のユーザー名/パスワードだけをメモリ上で保持する。
 *
 * /inventory/**(Amazonとのサーバー間通信)は既存のApiKeyFilterで別途保護しているため、
 * ここではCSRFチェックのみ対象外にして素通りさせる(認可の判断はApiKeyFilterに任せる)。
 * /products/**の3つのフォーム(登録・停止・再開)はCSRFトークンを送っていないため、
 * こちらもCSRFチェックの対象外にしている(管理者本人しか使わない画面のため許容)。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${dell.admin.username}")
    private String adminUsername;

    @Value("${dell.admin.password}")
    private String adminPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/products/**", "/inventory/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/inventory/**").permitAll()
                        .requestMatchers("/products/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
