package com.board.job.model.entity.sample;

import lombok.Getter;

@Getter
public enum WorkMode {
    CHOICE_OF_CANDIDATE("Choice of Candidate"), ONLY_OFFICE("Only Office"),
    ONLY_REMOTE("Only Remote"), HYBRID("Hybrid");

    private final String value;

    WorkMode(String value) {
        this.value = value;
    }

    public static WorkMode checkValuesAndGet(String insertedValue) {
        for (WorkMode workMode : WorkMode.values()) {
            if (insertedValue.equals(workMode.getValue())) {
                return workMode;
            }
        }
        throw new EnumConstantNotPresentException(LanguageLevel.class, "This work mode does not found!");
    }
}
