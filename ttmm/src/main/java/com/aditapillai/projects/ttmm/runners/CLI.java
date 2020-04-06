package com.aditapillai.projects.ttmm.runners;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class CLI implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        //TODO: Implement the CLI
        System.out.println("Here");
    }
}
