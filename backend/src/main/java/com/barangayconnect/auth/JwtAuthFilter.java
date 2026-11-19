package com.barangayconnect.auth;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwt;private final LoginSessionRepository sessions;
 public JwtAuthFilter(JwtService jwt,LoginSessionRepository sessions){this.jwt=jwt;this.sessions=sessions;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer "))try{Claims c=jwt.parse(h.substring(7));LoginSession s=sessions.findByJti(c.getId()).orElse(null);
   if(s!=null&&!s.isRevoked()&&s.getExpiresAt().isAfter(Instant.now())&&s.getLastActivityAt().isAfter(Instant.now().minusSeconds(1800))&&s.getUser().isActive()){
    s.setLastActivityAt(Instant.now());sessions.save(s);var a=new UsernamePasswordAuthenticationToken(s.getUser().getEmail(),null,List.of(new SimpleGrantedAuthority("ROLE_"+s.getUser().getRole().name())));SecurityContextHolder.getContext().setAuthentication(a);
   }
  }catch(Exception ignored){SecurityContextHolder.clearContext();}chain.doFilter(req,res);
 }
}