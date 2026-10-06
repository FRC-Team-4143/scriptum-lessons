public class FizzBuzzCheck {
    public static void main(String[] args) {
        check(1, "1");
        check(3, "1, 2, Fizz");
        check(15, "1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz");
        System.out.println("fizzBuzz looks good.");
    }

    static void check(int n, String expected) {
        String actual = Main.fizzBuzz(n);
        if (actual == null || !actual.equals(expected)) {
            System.out.println(
                "fizzBuzz(" + n + ") returned "
                    + (actual == null ? "null" : "\"" + actual + "\"")
                    + ", expected \"" + expected + "\"."
            );
            System.exit(1);
        }
    }
}
