public class StateLog {
    // How many entries in states are the same as wanted.
    // BUG (Watching a comparison): the log has three "INTAKE"s, but this counts 0. The debugger
    // shows the two strings looking exactly the same...
    public static int countState(String[] states, String wanted) {
        int count = 0;
        for (int i = 0; i < states.length; i++) {
            if (states[i] == wanted) {
                count++;
            }
        }
        return count;
    }
}
