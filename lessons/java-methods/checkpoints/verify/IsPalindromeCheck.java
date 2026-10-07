public class IsPalindromeCheck {
    public static void main(String[] args) {
        check("racecar", true);
        check("kayak", true);
        check("hello", false);
        check("a", true);
        check("", true);
        check("MARS/WARS", false);
        System.out.println("isPalindrome looks good.");
    }

    static void check(String s, boolean expected) {
        boolean actual = Main.isPalindrome(s);
        if (actual != expected) {
            System.out.println("isPalindrome(\"" + s + "\") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
