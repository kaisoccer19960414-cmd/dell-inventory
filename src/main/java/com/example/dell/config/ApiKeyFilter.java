package com.example.dell.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * サーバー間通信専用エンドポイント(/inventory/**)を守るための簡易フィルター。
 * Amazon(amazon-ec)からのリクエストだけを、共有のAPIキー(X-API-Keyヘッダー)で認証する。
 * ブラウザ向けの管理画面(/products等)はこのフィルターの対象外で、従来どおり無認証のまま
 * (Basic認証などの保護は別途検討)。
 *
 * dell-inventory全体にSpring Securityを導入するほどの規模ではないため、
 * 最小限のOncePerRequestFilterだけでこの穴を塞ぐ。
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${inventory.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/inventory")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
