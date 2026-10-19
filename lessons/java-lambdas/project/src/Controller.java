// A pretend Xbox controller. On a real robot the driver presses the buttons;
// here Main (and the checkpoints) "press" them with the set methods.
public class Controller {
    private boolean a_ = false;
    private boolean b_ = false;
    private boolean x_ = false;
    private double left_y_ = 0.0;

    public boolean a() {
        return a_;
    }

    public boolean b() {
        return b_;
    }

    public boolean x() {
        return x_;
    }

    // Like a real controller: pushing the stick forward gives a NEGATIVE number.
    public double getLeftY() {
        return left_y_;
    }

    public void setA(boolean pressed) {
        a_ = pressed;
    }

    public void setB(boolean pressed) {
        b_ = pressed;
    }

    public void setX(boolean pressed) {
        x_ = pressed;
    }

    public void setLeftY(double value) {
        left_y_ = value;
    }
}
