public class BadLineCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (RuntimeException.class.isAssignableFrom(BadLineException.class)) {
            fail("BadLineException still extends RuntimeException, so it's unchecked. Make it \"extends Exception\".");
        }
        if (!Exception.class.isAssignableFrom(BadLineException.class)) {
            fail("BadLineException should extend Exception.");
        }

        Exception e = new BadLineException("only 2 pieces", 17);
        if (!"only 2 pieces".equals(e.getMessage())) {
            fail("new BadLineException(\"only 2 pieces\", 17).getMessage() was " + quote(e.getMessage())
                + ", expected \"only 2 pieces\". Pass the message on with super(message).");
        }
        int line = ((BadLineException) e).getLineNumber();
        if (line != 17) {
            fail("new BadLineException(\"only 2 pieces\", 17).getLineNumber() was " + line + ", expected 17.");
        }
        BadLineException other = new BadLineException("bad team", 3);
        if (other.getLineNumber() != 3 || !"bad team".equals(other.getMessage())) {
            fail("new BadLineException(\"bad team\", 3) gave line " + other.getLineNumber() + " and message "
                + quote(other.getMessage()) + ", expected line 3 and \"bad team\".");
        }

        System.out.println("BadLineException is a checked exception that knows its line.");
    }

    static String quote(String s) {
        return s == null ? "null" : "\"" + s + "\"";
    }
}
