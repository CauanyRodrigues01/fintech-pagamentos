package com.fintech.pagamentos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidCharStatusValidator.class) // Indica qual classe implementa a validação
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE }) // Onde a anotação pode ser usada
@Retention(RUNTIME) // Quando a anotação estará disponível (em tempo de execução)
public @interface ValidCharStatus {

    String message() default "O valor do status é inválido. Valores permitidos: {allowedValues}."; // Mensagem padrão

    Class<?>[] groups() default {}; // Para agrupar validações

    Class<? extends Payload>[] payload() default {}; // Para transportar metadados para clientes da API

    // Atributo para definir os valores permitidos (ex: {'A', 'B'} ou {'P', 'A', 'B'})
    char[] allowedValues();
}