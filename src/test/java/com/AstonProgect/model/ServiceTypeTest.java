package com.AstonProgect.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceTypeTest {

    @Test
    void shouldContainAllExpectedServiceTypes() {
        assertThat(ServiceType.values())
                .containsExactly(
                        ServiceType.GUIDE,
                        ServiceType.CAR_TOUR,
                        ServiceType.FOOD,
                        ServiceType.ACCOMMODATION,
                        ServiceType.ENTERTAINMENT,
                        ServiceType.TRANSPORT
                );
    }

    @Test
    void shouldHaveCorrectOrder() {
        assertThat(ServiceType.values()[0])
                .isEqualTo(ServiceType.GUIDE);
    }

    @Test
    void shouldConvertToString() {
        assertThat(ServiceType.GUIDE.toString())
                .isEqualTo("GUIDE");
    }

    @Test
    void shouldParseFromString() {
        assertThat(ServiceType.valueOf("GUIDE"))
                .isEqualTo(ServiceType.GUIDE);
    }

    @Test
    void shouldHaveAllServiceTypesWithDescriptions() {
        for (ServiceType type : ServiceType.values()) {
            assertThat(type.toString()).isNotEmpty();
            assertThat(type.name()).isNotEmpty();
        }
    }
}