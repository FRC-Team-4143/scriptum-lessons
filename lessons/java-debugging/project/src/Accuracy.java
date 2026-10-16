public class Accuracy {
    // The percentage of shots that went in: 7 made of 8 attempted is 87.5.
    // No attempts at all counts as 0.0.
    // BUG (Step into a method): it always says 0.0. Step into it to see why.
    public static double percent(int made, int attempted) {
        if (attempted == 0) {
            return 0.0;
        }
        return made / attempted * 100;
    }
}
