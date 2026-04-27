package com.tibetan.medicine.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * JWT工具类 (使用 auth0 java-jwt)
 */
public class JwtUtil {
    
    private static final String SECRET = "tibetan-medicine-secret-key-2024-very-long-secret";
    private static final long EXPIRATION = 604800000L; // 7天
    
    /**
     * 获取签名算法
     */
    private static Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET);
    }
    
    /**
     * 生成Token
     */
    public static String generateToken(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(getAlgorithm());
    }

    /**
     * 生成Token（Long重载）
     */
    public static String generateToken(Long userId) {
        return generateToken(String.valueOf(userId));
    }

    /**
     * 解析Token
     */
    public static DecodedJWT parseToken(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }
    
    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 验证Token是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = parseToken(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
