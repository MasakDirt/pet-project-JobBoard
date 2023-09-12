package com.board.job.model.dto.employer_company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class EmployerCompanyResponse {
    private long id;

    @JsonProperty(value = "about_company")
    private String aboutCompany;

    @JsonProperty(value = "web_site")
    private String webSite;
}
