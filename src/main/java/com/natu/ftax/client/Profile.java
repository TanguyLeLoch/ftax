package com.natu.ftax.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.natu.ftax.common.exception.FunctionalException;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Profile {


    @Id
    @NotNull
    private String clientEmail;
    @Setter
    @NotNull
    @Enumerated(STRING)
    private CalculationMethod calculationMethod;

    enum CalculationMethod {
        FIFO("fifo"), AVERAGE("average");
        private final String value;

        CalculationMethod(String value) {
            this.value = value;
        }

        @JsonValue
        String getValue() {
            return value;
        }

        @JsonCreator
        public static CalculationMethod fromString(String value) {
            for (CalculationMethod method : CalculationMethod.values()) {
                if (method.value.equals(value)) {
                    return method;
                }
            }
            throw new FunctionalException("Invalid calculation method");
        }
    }
}
