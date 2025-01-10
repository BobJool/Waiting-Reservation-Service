package com.bobjool.gateway.infrastructure.jwt;

import com.bobjool.gateway.exception.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenParser {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String USER_ID_CLAIM_KEY = "userId";
	private static final String ROLE_CLAIM_KEY = "role";

	private final RedisService redisService;

	private SecretKey key;

	@Value("401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429080fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1")
	private String secretKey;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}

	public String getJwt(ServerHttpRequest request) {
		String jwt = Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				.orElseThrow(() -> new TokenException(ErrorCode.TOKEN_MISSING));

		if (!jwt.startsWith(BEARER_PREFIX)) {
			throw new TokenException(ErrorCode.INVALID_AUTH_HEADER);
		}

		return jwt.substring(BEARER_PREFIX.length());
	}

	public String getUserId(String token) {
		return getClaims(token, USER_ID_CLAIM_KEY);
	}

	public String getUserRole(String token) {
		return getClaims(token, ROLE_CLAIM_KEY);
	}

	private String getClaims(String token, String claimKey) {
		try {
			if (redisService.isBlackList(token)) {
				throw new TokenException(ErrorCode.TOKEN_BLACKLISTED);
			}

			Claims accessTokenClaims = parseClaims(token);

			String claim = accessTokenClaims.get(claimKey, String.class);
			if (claim == null) {
				throw new TokenException(ErrorCode.TOKEN_INVALID);
			}
			return claim;

		} catch (ExpiredJwtException e) {
			throw new TokenException(ErrorCode.TOKEN_EXPIRED);
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(ErrorCode.TOKEN_INVALID);
		}
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (ExpiredJwtException e) {
			throw new TokenException(ErrorCode.TOKEN_EXPIRED);
		} catch (Exception e) {
			throw new TokenException(ErrorCode.TOKEN_INVALID);
		}
	}
}
