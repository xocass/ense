package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.db.MongoConnection;
import gal.usc.etse.sharecloud.model.entity.Role;
import gal.usc.etse.sharecloud.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import java.util.Set;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@SpringBootApplication
public class Boot {
/*
         ¡OLLO! Antes de nada:
                 - Ter instalado jdk-21
                 - Declarar en terminales a emplear:
                            $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
                            $env:Path="$env:JAVA_HOME\bin;" + $env:Path
         Como runnear:
         1. Buildear desde root / do proxecto:          ./gradlew clean build
         2. Runnear en 1 terminal servidor primetro:    ./gradlew :server:bootRun
         3. Runnear en outra terminal cliente:          ./gradlew :client:run
    */

    public static void main(String[] args) {
        MongoConnection db= new MongoConnection();

        SpringApplication.run(Boot.class, args);
    }

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByRolename("USER") == null) {
                Role r = new Role();
                r.setRolename("USER");
                r.setPermissions(Set.of());
                r.setIncludes(Set.of());
                roleRepository.save(r);

                System.out.println(">>> Role USER creado automáticamente.");
            } else {
                System.out.println(">>> Role USER ya existe.");
            }
        };
    }
}
