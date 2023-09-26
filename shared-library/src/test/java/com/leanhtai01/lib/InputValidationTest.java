package com.leanhtai01.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InputValidationTest {
    @Test
    void testParseRangeIntegerChoiceFail() {
        assertThat(InputValidation.parseRangeIntegerChoice("abc")).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceEnumeratePatternSuccess() {
        assertThat(InputValidation.parseRangeIntegerChoice("1 2 3 4")).hasSize(4).contains(1, 2, 3, 4);
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternFail() {
        assertThat(InputValidation.parseRangeIntegerChoice("5-1")).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternSuccess() {
        assertThat(InputValidation.parseRangeIntegerChoice("1-5")).hasSize(5).contains(1, 2, 3, 4, 5);
    }
}
