package filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.project.app.config.JwtTokenProvider;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	try {
            // 헤더에서 JWT 받아오기
            String token = resolveToken((HttpServletRequest) request);
            
            // 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효하면 유저 정보를 가져와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response); // 여기서 예외가 발생할 수 있음
        } catch (IOException e) {
            // IO 관련 예외 처리
            throw e; // 또는 로깅 후 커스텀 응답 반환
        } catch (ServletException e) {
            // 서블릿 관련 예외 처리
            throw e; // 또는 로깅 후 커스텀 응답 반환
        } catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (RuntimeException e) {
            // 런타임 예외 처리 (토큰 검증 실패 등)
            throw e; // 또는 로깅 후 커스텀 응답 반환
        } finally {
            // 필요한 경우 SecurityContext 정리
            // SecurityContextHolder.clearContext();
        }
    }
    
    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}