/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.tellh.accessinline;

/**
 * DataSource<T> backed by a Producer<T>
 *
 * @param <T>
 */
public abstract class AbstractProducerToDataSourceAdapter<T> extends AbstractDataSource<T> {

    private final BaseConsumer consumer;

    public AbstractProducerToDataSourceAdapter() {
        consumer = createConsumer();
    }

    private BaseConsumer createConsumer() {
        return new BaseConsumer() {
            @Override
            protected void onProgressUpdateImpl(float progress) {
                AbstractProducerToDataSourceAdapter.this.setProgress(progress);
            }
        };
    }

}
