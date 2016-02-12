package thoth.model;

import hudson.ExtensionPoint;

/**
 * Strategy that decides which {@link Task} gets run on which {@link Service}.
 * @author Justina Chen
 * @since 16/2/12
 */
public abstract class LoadBalancer implements ExtensionPoint {

    public abstract int map(Task task, int sizeOfServers);

    public static final LoadBalancer ROUND_ROBIN = new LoadBalancer() {

        private int current = -1;

        @Override
        public int map(Task task, int sizeOfServers) {
            int next = current;
            do {
                next += 1;
                if (next == sizeOfServers) next = 0;
                current = next;
                return current;
            } while (next != current);
        }

    };

    protected LoadBalancer sanitize() {
        final LoadBalancer base = this;
        return new LoadBalancer() {
            @Override
            public int map(Task task, int sizeOfServers) {
                return base.map(task, sizeOfServers);
            }

            @Override
            protected LoadBalancer sanitize() { return this; }
        };
    }
}
