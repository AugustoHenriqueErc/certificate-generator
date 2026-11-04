package org.gratidaosempre.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidatorTest {
    @Test
    void validatorDateTestCase1() { assertEquals(true, Validator.dateTime("04-11-2026"));}

    @Test
    void validatorDateTestCase2() { assertEquals(true, Validator.dateTime("31-12-2026"));}

    @Test
    void validatorDateTestCase3() { assertEquals(false, Validator.dateTime("31-11-2026"));}

    @Test
    void validatorDateTestCase4() { assertEquals(false, Validator.dateTime("31-11-2026"));}
}
