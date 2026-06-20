package com.postech.challenge.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Placa {

    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    private final String value;

    private Placa(String value) {
        this.value = value;
    }

    public static Placa of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Placa is required");
        }

        String normalized = raw.toUpperCase().replace("-", "").trim();
        if (!PLACA_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid placa format. Expected pattern AAA0A00");
        }
        return new Placa(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Placa other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
