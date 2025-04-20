package kr.modusplant.domains.example;

import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    public String performBusinessLogic(boolean shouldThrowError) {
        if (shouldThrowError) {
            return "Business logic executed successfully!"; // 정상 흐름
        } else {
            throw new RuntimeException("Something went wrong during the business logic execution!"); // 예외 발생
        }
    }
}
