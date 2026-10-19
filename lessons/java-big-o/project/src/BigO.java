// The answer choices for Answers.java. You don't need to change this file.
// n is how many items the code works through (matches, teams, scores...).
public enum BigO {
    NOT_SURE,   // you haven't picked one yet
    CONSTANT,   // O(1): the same number of steps, however big n is
    LOG_N,      // O(log n): each step throws away half of what is left
    LINEAR,     // O(n): about one step for each item
    N_SQUARED,  // O(n²): about one step for every PAIR of items
}
