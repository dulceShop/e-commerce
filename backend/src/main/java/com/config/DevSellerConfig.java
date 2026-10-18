package com.config;
import com.entity.User;
import com.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration public class DevSellerConfig{
    @Bean @ConditionalOnProperty(name="app.demo.seller.enabled",havingValue="true")
    CommandLineRunner demoSeller(UserRepository users, PasswordEncoder encoder){return args-> {String email="Vendeur@maison-aurelie.test";
    if(users.findByEmail(email).isEmpty()){
        User u=new User();
        u.email=email;
        u.nom="Vendeur";
        u.prenom="Demo";
        u.passwordHash=encoder.encode("Vendeur123!");
        u.role=User.Role.VENDEUR;
        users.save(u);
    }



}};
}