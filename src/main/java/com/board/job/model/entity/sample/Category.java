package com.board.job.model.entity.sample;

public enum Category {
    JAVA, PYTHON, JAVASCRIPT, C_PLUS_PLUS, C_SHARP, PHP, RUBY, SWIFT, GO, TYPESCRIPT, KOTLIN, HTML, CSS, SQL,
    RUST, MATLAB, PERL, SHELL, OBJECTIVE_C, QA;

    @Override
    public String toString() {
        return switch (name()) {
            case "QA" -> "QA";
            case "PHP" -> "PHP";
            case "CSS" -> "CSS";
            case "SQL" -> "SQL";
            case "C_PLUS_PLUS" -> "C++";
            case "C_SHARP" -> "C#";
            case "JAVASCRIPT" -> "JavaScript";
            case "TYPESCRIPT" -> "TypeScript";
            case "OBJECTIVE_C" -> "Objective-C";
            default -> name().charAt(0) + name().substring(1).toLowerCase();
        };
    }
}
