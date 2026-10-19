public class PowerBudget {
    public static double totalAmps(PowerUser[] users) {
        double total = 0.0;
        for (PowerUser user : users) {
            total += user.getAmps();
        }
        return total;
    }

    public static String biggestUser(PowerUser[] users) {
        if (users.length == 0) {
            return "none";
        }
        PowerUser biggest = users[0];
        for (PowerUser user : users) {
            if (user.getAmps() > biggest.getAmps()) {
                biggest = user;
            }
        }
        return biggest.getName();
    }
}
