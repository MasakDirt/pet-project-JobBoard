package com.board.job.model.entity.employer;

import com.board.job.model.entity.User;
import com.board.job.model.entity.Vacancy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class EmployerCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "about_company", nullable = false)
    private String aboutCompany;

    @Column(name = "web_site", nullable = false)
    private String webSite;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @OneToOne(fetch = FetchType.EAGER)
    private User owner;

    @NotNull
    @JsonManagedReference
    @OneToMany(mappedBy = "employerCompany", cascade = CascadeType.ALL)
    private List<Vacancy> vacancies;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployerCompany that = (EmployerCompany) o;
        return id == that.id && Objects.equals(aboutCompany, that.aboutCompany) && Objects.equals(webSite, that.webSite)
                && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aboutCompany, webSite, owner);
    }

    @Override
    public String toString() {
        return "EmployerCompany{" +
                "id=" + id +
                ", aboutCompany='" + aboutCompany + '\'' +
                ", webSite='" + webSite + '\'' +
                ", owner=" + owner +
                '}';
    }
}
