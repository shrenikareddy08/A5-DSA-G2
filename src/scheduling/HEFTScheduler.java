package scheduling;

import model.Resource;
import model.Task;

public class HEFTScheduler {
    private final Resource[] resources;

    public HEFTScheduler(Resource[] resources) {
        this.resources = resources;
    }

    public Resource chooseResource(Task task) {
        Resource best = null;
        long bestFinish = Long.MAX_VALUE;

        for (Resource r : resources) {
            if (!r.canRun(task)) continue;

            long speed = Math.max(1, r.getTotalCpu());
            long predicted = Math.max(1, task.getEstimatedDurationMs() * 4 / speed);

            if (predicted < bestFinish) {
                bestFinish = predicted;
                best = r;
            }
        }
        return best;
    }
}
