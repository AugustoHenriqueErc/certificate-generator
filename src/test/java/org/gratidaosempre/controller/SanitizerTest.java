package org.gratidaosempre.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SanitizerTest {
    @Test
    void sanitizerNameTestCase1(){
       assertEquals("Augusto_Henrique",Sanitizer.replaceSpace("Augusto Henrique"));
    }
    @Test
    void sanitizerDateTestCase1() {
        assertEquals("30_08_2025", Sanitizer.replaceBar("30/08/2025"));
    }
    @Test
    void sanitizerNameTestCase2(){
        assertEquals("Augusto_Henrique",Sanitizer.replaceSpace(" Augusto Henrique "));
    }
    @Test
    void sanitizerDateTestCase2() {
        assertEquals("30_08_2025", Sanitizer.replaceBar(" 30/08/2025 "));
    }
}