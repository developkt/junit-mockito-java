package de.developkt.junit5demo;

import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {

    @BeforeEach
    public void runBefore() {
        System.out.println("before test");
    }

    @BeforeAll
    public void runBeforeAll() {
        System.out.println("run once before all tests");
    }

    @Test
    public void firstTest() {
        System.out.println("hello world");
    }

    @Test
    @Disabled
    public void secondTest() {
        System.out.println("hello world from second test");
    }

    @AfterEach
    public void runAfter() {
        System.out.println("after test");
    }

    @AfterAll
    public void runAfterAll() {
        System.out.println("run once after all tests");
    }
}
