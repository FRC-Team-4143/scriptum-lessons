public class StepQueueCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectNext(AutoQueue q, String want, String what) {
        String got = q.nextStep();
        if (!want.equals(got)) {
            fail(what + ": nextStep() was \"" + got + "\", expected \"" + want + "\".");
        }
    }

    static void expectLeft(AutoQueue q, int want, String what) {
        int got = q.stepsLeft();
        if (got != want) {
            fail(what + ": stepsLeft() was " + got + ", expected " + want + ".");
        }
    }

    static void run() {
        AutoQueue q = new AutoQueue();
        expectLeft(q, 0, "A new auto");
        expectNext(q, "done", "A new auto with no steps");

        q.addStep("leave zone");
        q.addStep("pick up");
        q.addStep("score high");
        expectLeft(q, 3, "After adding three steps");
        expectNext(q, "leave zone", "After adding leave zone, pick up, score high (the first step added runs first)");
        expectLeft(q, 2, "After running one step");
        q.addStep("park");
        expectNext(q, "pick up", "Second step");
        expectNext(q, "score high", "Third step");
        expectNext(q, "park", "A step added after the auto started (it goes at the back)");
        expectNext(q, "done", "After every step has run");
        expectNext(q, "done", "Asking again after every step has run");
        expectLeft(q, 0, "After every step has run");

        System.out.println("AutoQueue runs steps first in, first out.");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            fail("Your code crashed: " + e + ". Look for a pop, an index or a null that isn't checked first.");
        }
    }
}
