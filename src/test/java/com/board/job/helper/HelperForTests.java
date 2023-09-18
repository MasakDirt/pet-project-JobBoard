package com.board.job.helper;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    public static String getLoginToken(MockMvc mvc, String url, String email, String password) throws Exception {
        return mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(LoginRequest.of(email, password))
                        )
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
