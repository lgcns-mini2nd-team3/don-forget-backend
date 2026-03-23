package com.example.apigateway;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

// GlobalFilter는 implementation 'org.springframework.cloud:spring-cloud-starter-gateway' 의존성에서 제공하는 인터페이스입니다.
// GlobalFilter는 Spring Cloud Gateway에서 모든 요청에 대해 적용되는 필터를 정의하는 인터페이스입니다. GlobalFilter는 모든 라우트에 대해 적용되며, 요청과 응답을 가로채서 처리할 수 있습니다. GlobalFilter는 요청이 라우트로 전달되기 전에 실행되며, 요청을 수정하거나 인증, 로깅 등의 작업을 수행할 수 있습니다. GlobalFilter는 여러 개의 필터를 체인 형태로 연결하여 사용할 수 있습니다. GlobalFilter는 Mono<Void>를 반환하는 filter 메서드를 구현해야 합니다. filter 메서드는 ServerWebExchange와 GatewayFilterChain을 매개변수로 받아서 처리합니다.
@Component
public class JwtAuthentication implements GlobalFilter {
    @Value("${jwt.secret}") // .env 파일의 내용을 yml에서 사용하게 되고 이를 java에서 사용하는 연계
    private String secret;
    private Key key;
    // token 검증없이 통과하는 endpoint 등록
    private static final List<String> WHITE_LIST_PATHS = List.of(
        "/api/v1/auth/signup",
        "/api/v1/auth/login",
        "/health/alive"
    );

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @PostConstruct 
    private void init(){
        System.out.println(">>>> Provider jwt secret : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    //Mono는 reactive state한 데이터를 저장하기 위한 객체입니다. Mono는 0 또는 1개의 데이터를 저장할 수 있습니다. Mono는 비동기적으로 데이터를 처리할 수 있도록 도와줍니다.    
    // exchange는 요청과 응답과 데이터를 포함하는 객체입니다. exchange를 통해 요청의 헤더, 바디, 쿼리 파라미터 등을 가져올 수 있습니다. chain은 다음 필터로 요청을 전달하는 객체입니다. chain.filter(exchange)를 호출하면 다음 필터로 요청이 전달됩니다. filter 메서드에서는 JWT 토큰을 검증하는 로직을 구현할 수 있습니다. JWT 토큰이 유효한 경우에는 chain.filter(exchange)를 호출하여 다음 필터로 요청을 전달하고, 유효하지 않은 경우에는 예외를 발생시키거나 적절한 응답을 반환할 수 있습니다.
    // chain.filter(exchange)는 다음 필터로 요청을 전달하는 역할을 합니다. 만약 JWT 토큰이 유효하지 않은 경우에는 chain.filter(exchange)를 호출하지 않고 예외를 발생시키거나 적절한 응답을 반환하여 요청이 다음 필터로 전달되지 않도록 할 수 있습니다. 현재 구현에서는 JWT 토큰의 유효성 검증 로직이 간단하게 작성되어 있지만, 실제로는 JWT 라이브러리를 사용하여 토큰을 검증하는 로직을 추가하는 것이 일반적입니다.
    // exchange는 불변의 객체 이므로, 요청을 수정하려면 exchange.mutate()를 사용하여 새로운 ServerWebExchange 객체를 생성해야 합니다. 예를 들어, JWT 토큰에서 추출한 사용자 정보를 요청 헤더에 추가하려면 exchange.mutate().request(exchange.getRequest().mutate().header("X-USER-Id", email).build()).build()와 같이 작성할 수 있습니다. 이렇게 하면 새로운 ServerWebExchange 객체가 생성되고, 요청 헤더에 X-USER-Id가 추가된 상태로 다음 필터로 전달됩니다.
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println(">>>> JwtAuthentication init token vaidation :" + exchange);
        // 사용자의 요청을 가져와서 거기서 헤더의 Authorization의 첫번째 값을 가져옵니다. 이 값은 일반적으로 "Bearer <token 값>" 형태로 JWT 토큰이 포함되어 있습니다. 이 토큰을 검증하여 유효한지 확인하는 로직이 여기에 추가될 수 있습니다. 현재는 단순히 토큰 값을 출력하고 다음 필터로 요청을 전달하는 형태로 구현되어 있습니다.
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println(">>>> JwtAuthentication bearerToken : " + bearerToken);

        String endPoint = exchange.getRequest().getURI().getPath();
        System.out.println(">>> JwtAuthenticationFilter User EndPoint : " + endPoint);
        String method = exchange.getRequest().getMethod().name();
        System.out.println(">>> JwtAuthenticationFilter User Method : " + method);
        
        // 만약 엔드포인트가 화이트 리스트에 포함되어 있다면, 토큰 검증 없이 요청이 필터 통과 
        // if(WHITE_LIST_PATHS.contains(endPoint)){
        //     System.out.println(">>> JwtAuthenticationFilter filter WHITE_LIST_PATH pass : " + endPoint);
        //     return chain.filter(exchange);
        // }

        for (String whiteListPath : WHITE_LIST_PATHS) {
            if (pathMatcher.match(whiteListPath, endPoint)) {
                System.out.println(">>> JwtAuthenticationFilter WHITE_LIST_PATH pass : " + endPoint);
                return chain.filter(exchange);
            }
        }

        try {
            System.out.println(">>>> JwtAuthenticationFilter Authorization : " + bearerToken);
            if(bearerToken == null || !bearerToken.startsWith("Bearer ")){// authHeader가 없거나, 시작이 Bearer가 아닌 경우
                System.out.println(">>>> JwtAuthentication Not Authorization");
                throw new RuntimeException("JwtAuthentication Not Authorization");
            }
            // "Bearer " 이후의 토큰 부분을 추출
            String token = bearerToken.substring(7);
            System.out.println(">>>> JwtAuthenticationFilter token : " + token);
            
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
            // Jwts 만들 때 Subject에 email(사용자)를 저장했었음(사용자의 기본 정보가 될 수도 있음)
            String email = claims.getSubject();
            System.out.println(">>>> JwtAuthenticationFilter claims get email : " + email);
            
            // JwtProvider 의해서 Role이 입력된 경우에만 해당
            String role = claims.get("role", String.class);
            System.out.println(">>>> JwtAuthenticationFilter claims get role : " + role);

            Long userId = claims.get("user_id", Long.class);
            System.out.println(">>>> JwtAuthenticationFilter claims get user_id : " + userId);


            // X-USER-Id 변수로 email 값과 Role 추가
            // X는 custom header를 나타내는 접두사입니다. X-USER-Id는 사용자 이메일을 나타내는 커스텀 헤더

            // security context에 사용자 정보 저장하는 방법도 있음, 그러나 api-gateway는 Webflux 기반이므로 비동기 기반이므로 동기적인 처리인 context가 아닌 비동기 처리인 header에 사용자 정보를 저장하여 downstream service로 전달하는 방법을 선택
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder
                    .header("X-USER-Email", email)
                    .header("X-USER-Role", role)
                    .header("X-USER-ID", String.valueOf(userId))
                ).build();
                

            return chain.filter(modifiedExchange);


        } catch (Exception e) {
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
    }
    
}
