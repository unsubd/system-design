package com.aditapillai.projects.parkinglot.runners;

import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.services.LotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Profile("local")
public class CLI implements CommandLineRunner {
    private LotService service;
    private ApplicationContext context;
    private ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int selection = 0;
        while (selection != 4) {
            System.out.println("1: Allocate\n2: Register\n3: Release\n4: Quit");
            selection = Integer.parseInt(reader.readLine());
            this.print(this.processSelection(selection, reader)
                           .block());
        }
        SpringApplication.exit(context);
    }

    private void print(Object result) {
        try {
            System.out.println(this.mapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private Mono processSelection(int selection, BufferedReader reader) throws IOException {
        Mono result = Mono.just("");

        switch (selection) {
            case 1:
                System.out.println("Enter car number");
                result = this.service.allocate(reader.readLine());
                break;
            case 2:
                System.out.println("Enter car details in '<number,color,manufacturer>' format");
                result = this.service.register(new Car(reader.readLine()));
                break;
            case 3:
                System.out.println("Enter car number");
                result = this.service.release(reader.readLine());
                break;
        }

        return result;
    }

    @Autowired
    public void setService(LotService service) {
        this.service = service;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
