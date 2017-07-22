package net.sergey.diplom;

import net.sergey.diplom.services.parser.ParserAirfoil;
import net.sergey.diplom.services.stlgenerators.AirfoilStlGenerator;
import net.sergey.diplom.services.stlgenerators.Interpolation;
import net.sergey.diplom.services.stlgenerators.bezierinterpolation.BezierInterpolation;
import net.sergey.diplom.services.stlgenerators.cubespline.CubeSpline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan
@Configuration
@ImportResource({"classpath:beans.xml", "file:config/beans.xml"})
public class Application {
    @Autowired
    @Qualifier("parser_airfoil")
    private ParserAirfoil parserAirfoil;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AirfoilStlGenerator getAirfoilStlGenerator() {
        return new AirfoilStlGenerator() {
            @Override
            protected List<Interpolation> getInterpolators(List<String> beanName) {
                if (beanName.isEmpty()) {
                    beanName.add("cube");
                }
                List<Interpolation> interpolations = new ArrayList<>();
                beanName.forEach(s -> {
                    if ("cube".equals(s)) {
                        interpolations.add(getCubeSpline());
                    }
                    if ("bezier".equals(s)) {
                        interpolations.add(getBezierInterpolation());
                    }
                });
                return interpolations;
            }
        };
    }

    @Bean
    @Scope("prototype")
    public CubeSpline getCubeSpline() {
        return new CubeSpline();
    }

    @Bean
    @Scope("prototype")
    public BezierInterpolation getBezierInterpolation() {
        return new BezierInterpolation();
    }

    @PostConstruct
    public void init() {
        System.out.println(parserAirfoil.toString());

    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
        private final UserDetailsService userDetailsService;

        @Autowired
        public ApplicationSecurity(UserDetailsService userDetailsService) {this.userDetailsService = userDetailsService;}

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests()
                    .antMatchers("/add_airfoil", "/update_airfoil/**", "/store_airfoil", "/add_user",
                            "/update_database", "/init", "/stop", "/clearAll", "/adminka", "/api").hasRole("ADMIN")
                    .antMatchers("/**").permitAll().anyRequest()
                    .fullyAuthenticated().and().formLogin().loginPage("/login")
                    .failureUrl("/login?error").permitAll()
                    .defaultSuccessUrl("/")
                    .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                    .permitAll();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.jdbcAuthentication().dataSource(this.dataSource).passwordEncoder(new BCryptPasswordEncoder());
            auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
//             auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
        }
    }
}
