package com.board.job.helper;

import com.board.job.model.dto.user.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;

import java.util.Set;

public class HelperForTests {
    public static <T> Set<ConstraintViolation<T>> getViolation(T testedClass) {
        return Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(testedClass);
    }

    public static <T> String asJsonString(final T object) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(object);
        }catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    public static UserCreateRequest createUser(String firstName, String lastName, String email, String password) {
        return new UserCreateRequest(firstName, lastName, email, password);
    }
}
