public class ClassifyCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        String[] names = { "A", "B", "C", "D", "E", "F", "G", "H" };
        BigO[] picked = {
            Answers.SNIPPET_A, Answers.SNIPPET_B, Answers.SNIPPET_C, Answers.SNIPPET_D,
            Answers.SNIPPET_E, Answers.SNIPPET_F, Answers.SNIPPET_G, Answers.SNIPPET_H,
        };
        BigO[] want = {
            BigO.CONSTANT, BigO.LINEAR, BigO.N_SQUARED, BigO.LOG_N,
            BigO.CONSTANT, BigO.LINEAR, BigO.N_SQUARED, BigO.LINEAR,
        };
        String[] hints = {
            "Does jumping to the last item take longer when the list is longer?",
            "How many times does the loop body run for a list of n scores?",
            "For each i, how many j values does the inner loop try?",
            "Start with 1024 teams and count the rounds. Now try 2048. How many more rounds?",
            "Does this loop run more times when the event has more teams?",
            "Two loops one after the other take n steps, then n more. That's 2n, not n times n.",
            "contains() on an ArrayList has to check the list item by item. That's a loop hiding inside a loop.",
            "Asking a HashSet whether it has something takes about one step, however big it is.",
        };

        int right = 0;
        int first_wrong = -1;
        for (int i = 0; i < want.length; i++) {
            if (picked[i] == want[i]) {
                right++;
            } else if (first_wrong < 0) {
                first_wrong = i;
            }
        }
        if (first_wrong >= 0) {
            int i = first_wrong;
            if (picked[i] == null || picked[i] == BigO.NOT_SURE) {
                fail("Snippet " + names[i] + " in Answers.java is still NOT_SURE (" + right + " of 8 right so far). " + hints[i]);
            }
            fail("Snippet " + names[i] + " isn't " + picked[i] + " (" + right + " of 8 right so far). " + hints[i]);
        }
        System.out.println("All 8 snippets classified correctly.");
    }
}
