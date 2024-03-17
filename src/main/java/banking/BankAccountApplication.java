package me.jordaan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankAccountApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(BankAccountApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}