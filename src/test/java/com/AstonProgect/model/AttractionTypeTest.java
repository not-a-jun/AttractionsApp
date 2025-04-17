package com.AstonProgect.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AttractionTypeTest {

    @Test
    void shouldContainAllExpectedTypes() {
        assertThat(AttractionType.values())
                .containsExactly(
                        AttractionType.PALACE,
                        AttractionType.PARK,
                        AttractionType.MUSEUM,
                        AttractionType.ARCHAEOLOGICAL_SITE,
                        AttractionType.NATURE_RESERVE
                );
    }

    @Test
    void shouldParseFromString() {
        assertThat(AttractionType.valueOf("MUSEUM"))
                .isEqualTo(AttractionType.MUSEUM);
    }

    @Test
    void shouldReturnCorrectToString() {
        assertThat(AttractionType.MUSEUM.toString())
                .isEqualTo("MUSEUM");
    }

    @Test
    void shouldHaveDescriptiveNames() {
        assertThat(AttractionType.PALACE.name()).isEqualTo("PALACE");
        assertThat(AttractionType.PARK.name()).isEqualTo("PARK");
    }

    @Test
    void shouldHaveAllTypesWithDescriptions() {
        for (AttractionType type : AttractionType.values()) {
            assertThat(type.toString()).isNotEmpty();
            assertThat(type.name()).isNotEmpty();
        }
    }
}