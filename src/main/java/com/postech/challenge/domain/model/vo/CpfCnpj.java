package com.postech.challenge.domain.model.vo;

import java.util.Objects;

public final class CpfCnpj {

    private final String value;

    private CpfCnpj(String value) {
        this.value = value;
    }

    public static CpfCnpj of(String raw) {
        String normalized = normalize(raw);
        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ");
        }
        return new CpfCnpj(normalized);
    }

    public String value() {
        return value;
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    public static boolean isValid(String value) {
        String normalized = normalize(value);
        return isValidCpf(normalized) || isValidCnpj(normalized);
    }

    private static boolean isValidCpf(String cpf) {
        if (cpf.length() != 11 || hasAllEqualDigits(cpf)) {
            return false;
        }

        int firstDigit = calculateCpfDigit(cpf, 10);
        int secondDigit = calculateCpfDigit(cpf, 11);
        return firstDigit == Character.getNumericValue(cpf.charAt(9))
                && secondDigit == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calculateCpfDigit(String cpf, int weightStart) {
        int sum = 0;
        for (int i = 0; i < weightStart - 1; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (weightStart - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean isValidCnpj(String cnpj) {
        if (cnpj.length() != 14 || hasAllEqualDigits(cnpj)) {
            return false;
        }

        int firstDigit = calculateCnpjDigit(cnpj, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int secondDigit = calculateCnpjDigit(cnpj, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return firstDigit == Character.getNumericValue(cnpj.charAt(12))
                && secondDigit == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calculateCnpjDigit(String cnpj, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean hasAllEqualDigits(String value) {
        char first = value.charAt(0);
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CpfCnpj other)) {
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
