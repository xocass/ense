package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.db.Connection;
import gal.usc.etse.sharecloud.model.entity.Role;
import gal.usc.etse.sharecloud.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

//@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
//@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class Boot {

    /*  LANZAR DESDE CONSOLA:
    *   OLLO! Asegurarse de ter instalado jdk-21
    *   Declarar:
    *       $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
    *       $env:Path="$env:JAVA_HOME\bin;" + $env:Path
    *
    *   Build desde / do proxecto: ./gradlew clean build
    *   Run: java --add-opens java.base/java.lang=ALL-UNNAMED -jar build/libs/ShareCloud-0.0.1-SNAPSHOT.jar
    *
    */

    public static void main(String[] args) {
        Connection db= new Connection();

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
