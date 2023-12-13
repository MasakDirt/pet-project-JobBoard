package com.board.job.model.employer;

import com.board.job.helper.HelperForTests;
import com.board.job.model.entity.employer.EmployerCompany;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployerCompanyTests {
    private static EmployerCompany employerCompany;

    @BeforeAll
    public static void init() {
        employerCompany = new EmployerCompany();
        employerCompany.setId(1L);
        employerCompany.setAboutCompany("This is company...");
        employerCompany.setWebSite("company.dot.ua");
    }

    @Test
    public void testValidEmployerCompany() {
        Assertions.assertEquals(0, HelperForTests.getViolation(employerCompany).size());
    }
}
