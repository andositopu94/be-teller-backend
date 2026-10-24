package com.multipolar.uiteller.backend.security;

import com.multipolar.uiteller.backend.repository.ApiComponentRepository;
import com.multipolar.uiteller.backend.repository.ApiRoleRepository;
import com.multipolar.uiteller.backend.repository.RoleRepository;
import com.multipolar.uiteller.backend.repository.SessionTokenRepository;
import com.multipolar.uiteller.backend.service.AccNbrService;
import com.multipolar.uiteller.backend.servletfilter.RequestLoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationProvider")
    private AuthenticationProvider authenticationProvider;
 
    @Autowired
    private SessionTokenRepository sessionTokenRepository;
    
    @Autowired
    private ApiRoleRepository apiRoleRepository;
    
    @Autowired
    private ApiComponentRepository apiComponentRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    // @Autowired
    // private AdminChargeRepository chargeRepository;
    
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    
    @Autowired
    private RequestLoggerUtil loggerUtil;

    @Autowired
    private AccNbrService accNbrService;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().disable().and()
                .cors().and().csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
                // security permitall dowload pdf
                 .antMatchers("/downloads/pdf/**").permitAll()
                 .antMatchers(HttpMethod.POST, SecurityConstants.LOGOUT_URL).permitAll()
                .antMatchers("/apiv3/checkConnectionSisko").permitAll()
                .antMatchers("/apiv3/disconnectConnectionSisko").permitAll()
                .antMatchers("/apiv3/reconnectConnectionSisko").permitAll()
                .anyRequest().authenticated().and().addFilter(new JwtAuthenticationFilter(authenticationManager(), sessionTokenRepository, roleRepository, loggerUtil, accNbrService))
                .addFilter(
                		new JwtAuthorizationFilter(authenticationManager(), sessionTokenRepository, apiRoleRepository, apiComponentRepository))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .logout().logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll();
        ;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        List<String> headers = Arrays.asList("Content-Type", "Authorization");

        config.setAllowedOrigins(origins);
//        config.applyPermitDefaultValues();
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        config.setAllowedHeaders(headers);
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
