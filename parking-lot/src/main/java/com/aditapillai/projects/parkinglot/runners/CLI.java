package com.aditapillai.projects.parkinglot.runners;

import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CLI implements CommandLineRunner {
    private LotService service;
    private ApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int selection = 0;
        while (selection != 4) {
            System.out.println("1: Allocate\n2: Register\n3: Release\n4: Quit");
            selection = Integer.parseInt(reader.readLine());
            System.out.println(this.processSelection(selection, reader));
        }

        SpringApplication.exit(context);
    }

    private Object processSelection(int selection, BufferedReader reader) throws IOException {
        Object result = null;

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
}
