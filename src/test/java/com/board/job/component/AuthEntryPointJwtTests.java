package com.board.job.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthEntryPointJwtTests {
    private final MockMvc mvc;

    @Autowired
    public AuthEntryPointJwtTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void test_Injected_MockMvc() {
        assertNotNull(mvc);
    }

    @Test
    public void test_UnauthorizedError_Commence() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/unauthorized")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(
                        result -> assertEquals(
                                "Error: Unauthorized (please authorize before going to this URL).",
                                result.getResponse().getErrorMessage(),
                                "Result must return a valid message about 'unauthorized' user."
                        )
                );
    }
}