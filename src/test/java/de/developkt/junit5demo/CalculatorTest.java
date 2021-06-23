package de.developkt.junit5demo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator();
    }

    @Nested
    @DisplayName("all success cases for calculator class")
    class SuccessCases {

        @Test
        public void should_return_a_positive_sum_given_two_positive_integers() {
            Integer sum = calculator.positiveSum(3, 5);
            assertAll(
                    () -> assertTrue(sum > 0),
                    () -> assertEquals(8, sum),
                    () -> assertFalse(sum <= 0),
                    () -> assertNotNull(sum)
            );
        }

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({
                "1, 5, 6",
                "7, 3, 10"}
        )
        public void should_calculate_a_valid_sum_of_two_integers(int first, int second, int result) {
            int sum = calculator.positiveSum(first, second);
            assertEquals(result, sum);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 55, 98})
        public void should_add_input_value_to_0_and_return_input_value(int input) {
            int sum = calculator.positiveSum(0, input);
            assertEquals(input, sum);
        }

        @Test
        public void should_only_run_on_our_CI_system() {
            assumeTrue("true".equals(System.getenv("CI")));
            assertEquals(5, calculator.positiveSum(2, 3));
        }

        @TestFactory
        public List<DynamicNode> test_factory_for_x_plus_100_tests() {
            List<DynamicNode> list = new ArrayList<>(10);
            for (int i = 0; i <= 10; i++) {
                int randomInt = new Random().nextInt(100);
                list.add(
                        dynamicTest(
                                "should add " + randomInt + " to 100 and return " + (randomInt + 100),
                                () -> assertEquals(100 + randomInt, calculator.positiveSum(randomInt, 100))
                        )
                );
            }
            return list;
        }
    }
}