package thoth.model;

/**
 * User: Justina Chen
 * Date: 2/13/16
 * Time: 7:20 PM
 */
public class Service implements Task {
    @Override
    public boolean isBuildBlocked() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFullDisplayName() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public boolean isConcurrentBuild() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
