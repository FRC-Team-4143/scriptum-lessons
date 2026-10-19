public class Motor implements PowerUser {
    protected String name_;
    protected double amps_;

    public Motor(String name, double amps) {
        name_ = name;
        amps_ = amps;
    }

    @Override
    public String getName() {
        return name_;
    }

    @Override
    public double getAmps() {
        return amps_;
    }
}
