// How does each snippet grow as n gets bigger? Replace each NOT_SURE with
// CONSTANT, LOG_N, LINEAR or N_SQUARED (see BigO.java).
// In every snippet, n is the size of the list or array it works through.
public class Answers {
    // A. The most recent match score.
    //     int latest = scores.get(scores.size() - 1);
    public static final BigO SNIPPET_A = BigO.CONSTANT;

    // B. Add up every match score.
    //     int total = 0;
    //     for (int score : scores) {
    //         total += score;
    //     }
    public static final BigO SNIPPET_B = BigO.LINEAR;

    // C. Compare every team with every other team.
    //     for (int i = 0; i < teams.length; i++) {
    //         for (int j = 0; j < teams.length; j++) {
    //             if (i != j && teams[i] == teams[j]) { found = true; }
    //         }
    //     }
    public static final BigO SNIPPET_C = BigO.N_SQUARED;

    // D. An elimination bracket: every round, half the teams are knocked
    //    out, until one is left. n is the number of teams.
    //     int left = n;
    //     int rounds = 0;
    //     while (left > 1) {
    //         left = left / 2;
    //         rounds++;
    //     }
    public static final BigO SNIPPET_D = BigO.LOG_N;

    // E. Total the three robots on our alliance. teams has n teams in it,
    //    but alliance always has 3.
    //     int total = 0;
    //     for (int i = 0; i < 3; i++) {
    //         total += alliance[i];
    //     }
    public static final BigO SNIPPET_E = BigO.CONSTANT;

    // F. Two loops, one AFTER the other (not inside it).
    //     for (int score : scores) { total += score; }
    //     for (int score : scores) { if (score > best) { best = score; } }
    public static final BigO SNIPPET_F = BigO.LINEAR;

    // G. contains() on a List, inside a loop over a list of the same size.
    //     for (int team : picks) {
    //         if (scouted.contains(team)) { count++; }
    //     }
    //    (scouted is an ArrayList)
    public static final BigO SNIPPET_G = BigO.N_SQUARED;

    // H. The same loop as G, but scouted is now a HashSet.
    //     for (int team : picks) {
    //         if (scouted.contains(team)) { count++; }
    //     }
    public static final BigO SNIPPET_H = BigO.LINEAR;
}
