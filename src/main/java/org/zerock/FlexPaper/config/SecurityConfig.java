package org.zerock.FlexPaper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.zerock.FlexPaper.service.MemberService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*.csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트용)*/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/member/register", "/member/login").permitAll() // 로그인 페이지와 회원가입 페이지 접근 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증된 사용자만 허용
                )
                .formLogin(login -> login
                        .loginPage("/member/login") // 로그인 페이지 경로 설정
                        .defaultSuccessUrl("/bundle/list", true) // 로그인 성공 후 이동할 페이지 설정
                        .permitAll() // 로그인 페이지는 누구나 접근 가능
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 요청 URL
                        .logoutSuccessUrl("/member/login?logout") // 로그아웃 후 리디렉션
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 제거
                        .permitAll()
                );

        return http.build();
    }
}

