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
 * An abstract implementation of {@link DataSource} interface.
 *
 * <p> It is highly recommended that other data sources extend this class as it takes care of the
 * state, as well as of notifying listeners when the state changes.
 *
 * @param <T>
 */
public abstract class AbstractDataSource<T> implements DataSource<T> {
  /**
   * Describes state of data source
   */
  private enum DataSourceStatus {
    // data source has not finished yet
    IN_PROGRESS,

    // data source has finished with success
    SUCCESS,

    // data source has finished with failure
    FAILURE,
  }

  /**
   * Subclasses should invoke this method to set the progress.
   *
   * <p> This method will return {@code true} if the progress was successfully set, or
   * {@code false} if the data source has already been set, failed or closed.
   *
   * <p> This will also notify the subscribers if the progress was successfully set.
   *
   * <p> Do NOT call this method from a synchronized block as it invokes external code of the
   * subscribers.
   *
   * @param progress the progress in range [0, 1] to be set.
   * @return true if the progress was successfully set.
   */
  protected boolean setProgress(float progress) {
    return false;
  }

}
