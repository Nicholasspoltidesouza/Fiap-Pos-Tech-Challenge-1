package com.postech.challenge.application.validator;

import com.postech.challenge.domain.model.vo.CpfCnpj;

public final class CpfCnpjValidator {

    private CpfCnpjValidator() {
    }

    public static String normalize(String value) {
        return CpfCnpj.normalize(value);
    }

    public static boolean isValid(String value) {
        return CpfCnpj.isValid(value);
    }
}
