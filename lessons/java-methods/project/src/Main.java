public class Main {
    // Every robot subsystem needs to keep a motor output between -1.0 and
    // 1.0 - the drivetrain, the arm, the intake, all of them. Without a
    // method, you'd copy-paste the same min/max check everywhere you
    // needed it, and a bug found in one copy wouldn't get fixed in the
    // others. Write it once as a method, and every caller shares the one
    // correct copy.

    // TODO: write a method named clamp that takes three double parameters
    // (value, min, max) and returns value squeezed into the [min, max]
    // range: min if value is below min, max if it's above max, value
    // otherwise.

    // TODO: write a method named scaleJoystick that takes two double
    // parameters (rawInput, sensitivity) and returns rawInput multiplied
    // by sensitivity, clamped to [-1.0, 1.0]. Don't rewrite the min/max
    // check - call the clamp method you just wrote. That's the payoff:
    // a method isn't just a place to hide one calculation, it's a
    // building block the next method gets to reuse.

    // A few classics you'll run into in pretty much every intro
    // programming course - same loop/array skills you already have, just
    // wrapped in a method with a name instead of living in main.

    // TODO: write a method named isPrime that takes an int parameter n and
    // returns whether n is prime: true if n is 2 or greater and has no
    // divisors other than 1 and itself, false otherwise (including for n
    // less than 2).

    // TODO: write a method named factorial that takes an int parameter n
    // and returns n! (n * (n-1) * ... * 1), as an int. 0! is 1.

    // TODO: write a method named reverseString that takes a String
    // parameter s and returns a new String with its characters in reverse
    // order.

    // TODO: write a method named isPalindrome that takes a String
    // parameter s and returns whether it reads the same forwards and
    // backwards. Don't reverse it by hand a second time - call the
    // reverseString method you just wrote and compare.

    public static void main(String[] args) {
        // TODO: try your methods out here - call them with a few
        // different inputs and click Run to see the output, same as
        // previous lessons.
    }
}
