package no.priv.bang.ukelonn.mocks;

import no.priv.bang.ukelonn.UkelonnDatabase;

public abstract class UkelonnDatabaseRecordingUnlockCall implements UkelonnDatabase {

    private boolean forceReleaseLocksCalled;

    public UkelonnDatabaseRecordingUnlockCall() {
        this.forceReleaseLocksCalled = false;
    }

    public boolean isForceReleaseLocksCalled() {
        return forceReleaseLocksCalled;
    }

    @Override
    public void forceReleaseLocks() {
        forceReleaseLocksCalled = true;
    }

}
