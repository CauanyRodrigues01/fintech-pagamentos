package com.fintech.pagamentos.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidCharStatusValidatorTest {

    private ValidCharStatusValidator validator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private ValidCharStatus validCharStatusAnnotation;

    @BeforeEach
    void setUp() {
        validator = new ValidCharStatusValidator();

        lenient().when(validCharStatusAnnotation.allowedValues()).thenReturn(new char[]{'A', 'B'});

        ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        lenient().when(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                .thenReturn("O valor do status é inválido. Valores permitidos: {allowedValues}.");

        lenient().when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);

        lenient().when(violationBuilder.addConstraintViolation()).thenReturn(null);

        validator.initialize(validCharStatusAnnotation);
    }

    @Test
    @DisplayName("isValid - Deve retornar true para caractere válido ('A')")
    void isValid_ShouldReturnTrueForValidCharA() {
        // GIVEN: O validador está configurado para {'A', 'B'}
        Character validChar = 'A';

        // WHEN: Valida o caractere
        boolean isValid = validator.isValid(validChar, constraintValidatorContext);

        // THEN: O resultado deve ser verdadeiro
        assertTrue(isValid);
        // Nenhuma interação com o contexto de erro esperada para um teste válido
        verify(constraintValidatorContext, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("isValid - Deve retornar true para caractere válido ('B')")
    void isValid_ShouldReturnTrueForValidCharB() {
        // GIVEN: O validador está configurado para {'A', 'B'}
        Character validChar = 'B';

        // WHEN: Valida o caractere
        boolean isValid = validator.isValid(validChar, constraintValidatorContext);

        // THEN: O resultado deve ser verdadeiro
        assertTrue(isValid);
        verify(constraintValidatorContext, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("isValid - Deve retornar false para caractere inválido ('X')")
    void isValid_ShouldReturnFalseForInvalidChar() {
        // GIVEN: O validador está configurado para {'A', 'B'}
        Character invalidChar = 'X';

        // WHEN: Valida o caractere
        boolean isValid = validator.isValid(invalidChar, constraintValidatorContext);

        // THEN: O resultado deve ser falso
        assertFalse(isValid);
        // Verifica que o contexto de erro foi utilizado para construir a mensagem
        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    @DisplayName("isValid - Deve retornar true para valor nulo (tratado por @NotNull)")
    void isValid_ShouldReturnTrueForNullValue() {
        // GIVEN: Um valor nulo (a anotação @NotNull é que deve lidar com isso, não este validador)
        Character nullChar = null;

        // WHEN: Valida o caractere nulo
        boolean isValid = validator.isValid(nullChar, constraintValidatorContext);

        // THEN: O resultado deve ser verdadeiro
        assertTrue(isValid);
        verify(constraintValidatorContext, never()).disableDefaultConstraintViolation();
    }

    @Test
    @DisplayName("isValid - Deve funcionar com diferentes valores permitidos (ex: 'P','A','B' para Fatura)")
    void isValid_ShouldWorkWithDifferentAllowedValues() {
        // GIVEN: Re-inicializa o validador para outro conjunto de valores
        when(validCharStatusAnnotation.allowedValues()).thenReturn(new char[]{'P', 'A', 'B'});
        validator.initialize(validCharStatusAnnotation); // Re-inicializa

        Character validCharP = 'P';
        Character validCharA = 'A';
        Character validCharB = 'B';
        Character invalidCharY = 'Y';

        // WHEN & THEN para valores válidos
        assertTrue(validator.isValid(validCharP, constraintValidatorContext));
        assertTrue(validator.isValid(validCharA, constraintValidatorContext));
        assertTrue(validator.isValid(validCharB, constraintValidatorContext));

        // WHEN & THEN para valor inválido
        assertFalse(validator.isValid(invalidCharY, constraintValidatorContext));
        // Verify context interaction for the failed validation only
        verify(constraintValidatorContext, times(1)).disableDefaultConstraintViolation(); // Only one failure in this sequence
    }
}