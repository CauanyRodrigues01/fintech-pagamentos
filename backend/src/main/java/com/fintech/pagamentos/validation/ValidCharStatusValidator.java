package com.fintech.pagamentos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidCharStatusValidator implements ConstraintValidator<ValidCharStatus, Character> {

    private char[] allowedValues; // Array para armazenar os valores permitidos

    @Override
    public void initialize(ValidCharStatus constraintAnnotation) {
        this.allowedValues = constraintAnnotation.allowedValues(); // Inicializa com os valores definidos na anotação
    }

    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull já cuida de valores nulos, então se for nulo, consideramos válido aqui
        }

        // Verifica se o caractere recebido está na lista de valores permitidos
        for (char allowedChar : allowedValues) {
            if (value == allowedChar) {
                return true;
            }
        }

        // Se o valor não for encontrado, a validação falha.
        // Adiciona o valor dos "allowedValues" na mensagem de erro
        context.disableDefaultConstraintViolation(); // Desabilita a mensagem padrão
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate()
                        .replace("{allowedValues}", Arrays.toString(allowedValues))
        ).addConstraintViolation();

        return false;
    }
}
