package com.config;
import com.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Configuration public class SecurityConfig {
 @Value("${app.jwt.secret}") String secret;
  @Value("${app.cors.origin}") String origin;
 public SecretKey key(){return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 @Bean PasswordEncoder encoder(){
    return new BCryptPasswordEncoder();
}
 @Bean CorsConfigurationSource cors(){
    CorsConfiguration c=new CorsConfiguration(); 
    c.setAllowedOrigins(List.of(origin));
     c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); 
     c.setAllowedHeaders(List.of("*"));
      c.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();
      s.registerCorsConfiguration("/**",c);return s;}
 @Bean SecurityFilterChain security(HttpSecurity http,UserRepository users)throws Exception {
    var filter=new OncePerRequestFilter(){
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws java.io.IOException,ServletException {String h=request.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer ")){try{String email=Jwts.parser().verifyWith(key()).build().parseSignedClaims(h.substring(7)).getPayload().getSubject();users.findByEmail(email).filter(u->u.actif).ifPresent(u->SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u.email,null,List.of(new SimpleGrantedAuthority("ROLE_"+u.role.name())))));}catch(Exception ignored){}}chain.doFilter(request,response);}};return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/api/products/**","/api/categories/**","/api/reviews/product/**").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/api/seller/**").hasAnyRole("VENDEUR","ADMIN").anyRequest().authenticated()).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();}
 public String token(String email){
    return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+86400000)).signWith(key()).compact();
}
}
