public class StateLog {
    // How many entries in states are the same as wanted.
    public static int countState(String[] states, String wanted) {
        int count = 0;
        for (int i = 0; i < states.length; i++) {
            if (states[i].equals(wanted)) {
                count++;
            }
        }
        return count;
    }
}
