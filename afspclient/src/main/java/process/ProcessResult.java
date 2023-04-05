package process;

import java.util.List;

public class ProcessResult {
    List<String> target;

    public ProcessResult(List<String> target) {
        this.target = target;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }
}
