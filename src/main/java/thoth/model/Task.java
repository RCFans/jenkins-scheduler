package thoth.model;

import hudson.model.ModelObject;

/**
 * Task whose execution is controlled by the queue.
 *
 * <p>
 * {@link #equals(Object) Value equality} of {@link Task}s is used
 * to collapse two tasks into one. This is used to avoid infinite
 * queue backlog.
 *
 * <p>
 * Pending {@link Task}s are persisted when Hudson shuts down, so
 * it needs to be persistable via XStream. To create a non-persisted
 * transient Task, extend {@link TransientTask} marker interface.
 *
 * <p>
 * Plugins are encouraged to extend from {@link AbstractQueueTask}
 * instead of implementing this interface directly, to maintain
 * compatibility with future changes to this interface.
 *
 * <p>
 * Plugins are encouraged to implement {@link AccessControlled} otherwise
 * the tasks will be hidden from display in the queue.
 *
 * <p>
 * For historical reasons, {@link Task} object by itself
 * also represents the "primary" sub-task (and as implied by this
 * design, a {@link Task} must have at least one sub-task.)
 * Most of the time, the primary subtask is the only sub task.
 * @author Justina Chen
 * @since 16/2/12
 */
public interface Task extends ModelObject {
    /**
     * Returns true if the execution should be blocked
     * for temporary reasons.
     *
     * <p>
     * Short-hand for {@code getCauseOfBlockage()!=null}.
     */
    boolean isBuildBlocked();

    /**
     * If the execution of this task should be blocked for temporary reasons,
     * this method returns a non-null object explaining why.
     *
     * <p>
     * Otherwise this method returns null, indicating that the build can proceed right away.
     *
     * <p>
     * This can be used to define mutual exclusion that goes beyond
     * {@link #getResourceList()}.
     */
    // todo
//    CauseOfBlockage getCauseOfBlockage();

    /**
     * Unique name of this task.
     *
     * <p>
     * This method is no longer used, left here for compatibility. Just return {@link #getDisplayName()}.
     */
    String getName();

    /**
     * @see hudson.model.Item#getFullDisplayName()
     */
    String getFullDisplayName();

    /**
     * Returns the URL of this task relative to the context root of the application.
     *
     * <p>
     * When the user clicks an item in the queue, this is the page where the user is taken to.
     * Hudson expects the current instance to be bound to the URL returned by this method.
     *
     * @return
     *      URL that ends with '/'.
     */
    String getUrl();

    /**
     * True if the task allows concurrent builds, where the same {@link Task} is executed
     * by multiple executors concurrently on the same or different nodes.
     *
     * @since 1.338
     */
    boolean isConcurrentBuild();
}
