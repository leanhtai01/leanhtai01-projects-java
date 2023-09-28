package com.leanhtai01.lib;

import static com.leanhtai01.lib.InputValidation.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

class InputValidationTest {
    @Test
    void testIsAnswerYesSingleLowercaseCharacter() {
        assertThat(isAnswerYes("y")).isTrue();
    }

    @Test
    void testIsAnswerYesSingleUppercaseCharacter() {
        assertThat(isAnswerYes("Y")).isTrue();
    }

    @Test
    void testIsAnswerYesFullTextLowercase() {
        assertThat(isAnswerYes("yes")).isTrue();
    }

    @Test
    void testIsAnswerYesFullTextUppercase() {
        assertThat(isAnswerYes("YES")).isTrue();
    }

    @Test 
    void testIsAnswerYesAnswerNo() {
        assertThat(isAnswerYes("no")).isFalse();
    }

    @Test
    void testIsAnswerYesBlank() {
        assertThat(isAnswerYes("")).isTrue();
    }

    @Test
    void testIsAnswerYesRandomInput() {
        assertThat(isAnswerYes("abc")).isFalse();
    }

    @Test
    void testIsValidIntegerChoicesTrue() {
        assertThat(isValidIntegerChoices(Set.of(1, 2, 3), 1, 3)).isTrue();
    }

    @Test
    void testIsValidIntegerChoicesOutOfBoundFalse() {
        assertThat(isValidIntegerChoices(Set.of(1, 2, 3), 1, 2)).isFalse();
    }

    @Test
    void testIsValidIntegerChoicesEmptyChoicesFalse() {
        assertThat(isValidIntegerChoices(Set.of(), 1, 2)).isFalse();
    }

    @Test
    void testParseRangeIntegerChoiceInvalidInput() {
        assertThat(parseRangeIntegerChoice("abc", 1, 2)).isEmpty();
    }

    @Test
    void testParseRangeIntegerChoiceEnumerateInput() {
        assertThat(parseRangeIntegerChoice("1 2 3", 1, 3)).hasSize(3).contains(1, 2, 3);
    }

    @Test
    void testParseRangeIntegerChoiceRangeInput() {
        assertThat(parseRangeIntegerChoice("1-5", 1, 5)).hasSize(5).contains(1, 2, 3, 4, 5);
    }

    @Test
    void testParseRangeIntegerChoiceBlankInput() {
        assertThat(parseRangeIntegerChoice("", 1, 5)).hasSize(5).contains(1, 2, 3, 4, 5);
    }

    @Test
    void testGetAllIntegerChoicesSuccess() {
        assertThat(getAllIntegerChoices(1, 5)).hasSize(5).contains(1, 2, 3, 4, 5);
    }

    @Test
    void testGetAllIntegerChoicesFail() {
        assertThat(getAllIntegerChoices(5, 1)).isEmpty();
    }
}
