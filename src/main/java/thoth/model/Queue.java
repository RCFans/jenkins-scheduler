package thoth.model;

import javax.annotation.Nonnull;
import hudson.init.Initializer;
import static hudson.init.InitMilestone.JOB_LOADED;
import hudson.model.ResourceController;
import hudson.model.Saveable;

import java.io.IOException;

/**
 * Build queue.
 *
 * <p>
 * This class implements the core scheduling logic. {@link Task} represents the executable
 * task that are placed in the queue. While in the queue, it's wrapped into {@link Item}
 * so that we can keep track of additional data used for deciding what to execute when.
 *
 * <p>
 * Items in queue goes through several stages, as depicted below:
 * <pre>
 * (enter) --> waitingList --+--> blockedProjects
 *                           |        ^
 *                           |        |
 *                           |        v
 *                           +--> buildables ---> pending ---> left
 *                                    ^              |
 *                                    |              |
 *                                    +---(rarely)---+
 * </pre>
 *
 * <p>
 * Note: In the normal case of events pending items only move to left. However they can move back
 * if the node they are assigned to execute on disappears before their {@link Executor} thread
 * starts, where the node is removed before the {@link Executable} has been instantiated it
 * is safe to move the pending item back to buildable. Once the {@link Executable} has been
 * instantiated the only option is to let the {@link Executable} bomb out as soon as it starts
 * to try an execute on the node that no longer exists.
 *
 * <p>
 * In addition, at any stage, an item can be removed from the queue (for example, when the user
 * cancels a job in the queue.) See the corresponding field for their exact meanings.
 *
 * @author Justina Chen
 * @since 16/2/12
 */
public class Queue extends ResourceController implements Saveable {

    private volatile transient LoadBalancer loadBalancer;

    public Queue(@Nonnull LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer.sanitize();
    }

    @Override
    public void save() throws IOException {
    }

    private void load() {
    }

    @Initializer(after=JOB_LOADED)
    public static void init(Thoth t) {
        t.getQueue().load();
    }
}
