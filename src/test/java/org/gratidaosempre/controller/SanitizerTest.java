package org.gratidaosempre.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SanitizerTest {
    @Test
    void sanitizerNameTest(){
       assertEquals("Augusto_Henrique",Sanitizer.replaceSpace("Augusto Henrique"));
    }
    @Test
    void sanitizerDateTest() {
        assertEquals("30_08_2025", Sanitizer.replaceBar("30_08_2025"));
    }
    @Test
    void sanitizerNameTest1(){
        assertEquals("Augusto_Henrique",Sanitizer.replaceSpace(" Augusto Henrique "));
    }
    @Test
    void sanitizerDateTest1() {
        assertEquals("30_08_2025", Sanitizer.replaceBar(" 30_08_2025 "));
    }
}