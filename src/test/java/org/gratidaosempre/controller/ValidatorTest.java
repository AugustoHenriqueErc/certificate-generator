package org.gratidaosempre.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"04-11-2026", "31-12-2026", "29-02-2024", "04/11/2026", "29/02/2000"})
    void acceptsRealDates(String date) {
        assertTrue(Validator.dateTime(date));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "31-11-2026", "29-02-2025", "29/02/1900", "31/04/2026", "00-01-2026",
            "01-00-2026", "01-13-2026", "01-01-0000", "4-11-2026", "04-1-2026", "2026-11-04",
            "04/11-2026", " 04-11-2026 ", "04-11-2026\n", "04-11-2026abc"})
    void rejectsInvalidDatesAndFormats(String date) {
        assertFalse(Validator.dateTime(date));
    }
}
