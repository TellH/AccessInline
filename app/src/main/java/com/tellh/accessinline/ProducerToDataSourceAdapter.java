package com.tellh.accessinline;

public class ProducerToDataSourceAdapter extends AbstractProducerToDataSourceAdapter {
    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public Throwable getFailureCause() {
        return null;
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public boolean close() {
        return false;
    }
}
