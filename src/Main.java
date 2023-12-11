import TestFramework.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        TestProccessor.runTest(MyTestClass.class);
    }

    static class MyTestClass {

        @BeforeAll
        public void beforeAll() {
            System.out.println("BEFORE ALL METHOD RUN");
        }

        @BeforeEach
        public void beforeEach() {
            System.out.println("BEFORE EACH METHOD RUN");
        }

        @AfterAll
        public void afterAll() {
            System.out.println("AFTER ALL METHOD RUN");
        }

        @AfterEach
        public void afterEach() {
            System.out.println("AFTER EACH METHOD RUN");
        }

        @Test
        @Order(1)
        public void testOne() {
            System.out.println("The first test run");
        }

        @Test
        @Order(0)
        public void testTwo() {
            System.out.println("The second test run");
        }



    }
}