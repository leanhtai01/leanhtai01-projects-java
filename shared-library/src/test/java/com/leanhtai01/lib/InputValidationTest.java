package com.leanhtai01.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import static com.leanhtai01.lib.InputValidation.*;

class InputValidationTest {
    @Test
    void testParseRangeIntegerChoiceInvalidInput() {
        assertThat(parseRangeIntegerChoice("abc", 1, 3)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceEnumeratePatternSuccess() {
        assertThat(parseRangeIntegerChoice("1 2 3 4", 1, 4)).hasSize(4).contains(1, 2, 3, 4);
    }

    @Test
    void testParseRangeIntegerChoiceEnumeratePatternOutOfBound() {
        assertThat(parseRangeIntegerChoice("1 2 3 4", 1, 2)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceEnumeratePatternUnorderedSuccess() {
        assertThat(parseRangeIntegerChoice("4 1 3 2", 1, 4)).hasSize(4).contains(1, 2, 3, 4);
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternReverse() {
        assertThat(parseRangeIntegerChoice("5-1", 1, 5)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternSuccess() {
        assertThat(parseRangeIntegerChoice("1-5", 1, 5)).hasSize(5).contains(1, 2, 3, 4, 5);
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternMinChoiceOutOfBound() {
        assertThat(parseRangeIntegerChoice("1-5", 2, 5)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceRangePatternMaxChoiceOutOfBound() {
        assertThat(parseRangeIntegerChoice("1-5", 1, 4)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceOnlyOneNumber() {
        assertThat(parseRangeIntegerChoice("1", 1, 2)).hasSize(1).contains(1);
    }
}
