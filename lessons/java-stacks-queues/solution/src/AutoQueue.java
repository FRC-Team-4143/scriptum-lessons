// The steps of an autonomous routine, run one after another in the order
// they were added: the first step added is the first step run.
import java.util.ArrayDeque;

public class AutoQueue {
    protected ArrayDeque<String> steps_ = new ArrayDeque<>();

    public void addStep(String step) {
        steps_.offer(step);
    }

    public String nextStep() {
        if (steps_.isEmpty()) {
            return "done";
        }
        return steps_.poll();
    }

    public int stepsLeft() {
        return steps_.size();
    }
}
