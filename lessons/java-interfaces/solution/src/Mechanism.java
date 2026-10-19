public abstract class Mechanism implements PowerUser {
    protected String name_;

    public Mechanism(String name) {
        name_ = name;
    }

    @Override
    public String getName() {
        return name_;
    }

    public boolean isOverLimit(double limit_amps) {
        return getAmps() > limit_amps;
    }
}
