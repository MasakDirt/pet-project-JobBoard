package com.board.job.model.entity.sample;

import lombok.Getter;

@Getter
public enum Category {
    JAVA("Java"), PYTHON("Python"), JAVASCRIPT("JavaScript"), C_PLUS_PLUS("C++"), C_SHARP("C#"),
    PHP("PHP"), RUBY("Ruby"), SWIFT("Swift"), GO("Go"), TYPESCRIPT("TypeScript"),
    KOTLIN("Kotlin"), HTML("HTML"), CSS("CSS"), SQL("SQL"), RUST("Rust"),
    MATLAB("Matlab"), PERL("Perl"), SHELL("SHELL"), OBJECTIVE_C("Objective-C"), QA("QA");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static Category checkValuesAndGet(String insertedValue) {
        for (Category category : Category.values()) {
            if (insertedValue.equals(category.getValue())) {
                return category;
            }
        }
        throw new EnumConstantNotPresentException(Category.class, "This category does not found!");
    }
}
