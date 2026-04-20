package com.postech.challenge.application.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CpfCnpjValidatorTest {

    @Test
    void shouldNormalizeNullAsEmptyString() {
        assertEquals("", CpfCnpjValidator.normalize(null));
    }

    @Test
    void shouldNormalizeCpfAndCnpjKeepingOnlyDigits() {
        assertEquals("52998224725", CpfCnpjValidator.normalize("529.982.247-25"));
        assertEquals("11222333000181", CpfCnpjValidator.normalize("11.222.333/0001-81"));
    }

    @Test
    void shouldValidateValidCpf() {
        assertTrue(CpfCnpjValidator.isValid("529.982.247-25"));
        assertTrue(CpfCnpjValidator.isValid("11144477735"));
    }

    @Test
    void shouldRejectInvalidCpf() {
        assertFalse(CpfCnpjValidator.isValid("529.982.247-24"));
        assertFalse(CpfCnpjValidator.isValid("11111111111"));
        assertFalse(CpfCnpjValidator.isValid("1234567890"));
    }

    @Test
    void shouldValidateValidCnpj() {
        assertTrue(CpfCnpjValidator.isValid("11.222.333/0001-81"));
        assertTrue(CpfCnpjValidator.isValid("11444777000161"));
    }

    @Test
    void shouldRejectInvalidCnpj() {
        assertFalse(CpfCnpjValidator.isValid("11.222.333/0001-82"));
        assertFalse(CpfCnpjValidator.isValid("00000000000000"));
        assertFalse(CpfCnpjValidator.isValid("1122233300018"));
    }
}
