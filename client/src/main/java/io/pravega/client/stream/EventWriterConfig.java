/**
 * Copyright Pravega Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pravega.client.stream;

import com.google.common.base.Preconditions;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventWriterConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    /**
     * Initial backoff in milli seconds used in the retry logic of the writer. The default value is 1ms.
     *
     * @param initialBackoffMillis Initial backoff in milli seconds in the retry logic of the writer.
     * @return Initial backoff in milli seconds used in the retry logic of the writer.
     */
    private final int initialBackoffMillis;

    /**
     * Maximum backoff in milli seconds used in the retry logic of the writer. The default value is 20000.
     *
     * @param maxBackoffMillis Maximum backoff in milli seconds used in the retry logic of the writer.
     * @return Maximum backoff in milli seconds used in the retry logic of the writer.
     */
    private final int maxBackoffMillis;

    /**
     * Maximum retry attempts performed by the writer before throwing a {@link io.pravega.common.util.RetriesExhaustedException}.
     * The default value is 10.
     *
     * @param retryAttempts Maximum retry attempts performed by the writer before throwing a {@link io.pravega.common.util.RetriesExhaustedException}.
     * @return Maximum retry attempts performed by the writer before throwing a {@link io.pravega.common.util.RetriesExhaustedException}.
     */
    private final int retryAttempts;

    /**
     * Backoff multiplier used in the retry logic of the writer. The default value is 10.
     *
     * @param backoffMultiple Backoff multiplier used in the retry logic of the writer.
     * @return Backoff multiplier used in the retry logic of the writer.
     */
    private final int backoffMultiple;

    /**
     * Enable or disable connection pooling for writer. The default value is false.
     *
     * @param enableConnectionPooling Enable or disable connection pooling for writer.
     * @return Enable or disable connection pooling for writer.
     */
    private final boolean enableConnectionPooling;

    /*
     * The transaction timeout parameter corresponds to the lease renewal period.
     * In every period, the client must send at least one ping to keep the txn alive.
     * If the client fails to do so, then Pravega aborts the txn automatically. The client
     * sends pings internally and requires no application intervention, only that it sets
     * this parameter accordingly.
     *
     * This parameter is additionally used to determine the total amount of time that
     * a txn can remain open. Currently, we set the maximum amount of time for a
     * txn to remain open to be the minimum between 1 day and 1,000 times the value
     * of the lease renewal period. The 1,000 is hardcoded and has been chosen arbitrarily
     * to be a large enough value.
     *
     * The maximum allowed lease time by default is 600s, see:
     *
     * {@link io.pravega.controller.util.Config.PROPERTY_TXN_MAX_LEASE}
     *
     * The maximum allowed lease time is a configuration parameter of the controller
     * and can be changed accordingly. Note that being a controller-wide parameter,
     * it affects all transactions.
     */
    private final long transactionTimeoutTime;

    /**
     * Automatically invoke {@link EventStreamWriter#noteTime(long)} passing
     * {@link System#currentTimeMillis()} on a regular interval.
     *
     * @param automaticallyNoteTime Interval to regularly invoke {@link EventStreamWriter#noteTime(long)}.
     * @return Interval to regularly invoke {@link EventStreamWriter#noteTime(long)}.
     */
    private final boolean automaticallyNoteTime;

    /**
     * Enable or disable whether LargeEvent writes should be processed and sent to the SegmentStore. A LargeEvent
     * is defined as any event containing a number of bytes greater than {@link Serializer.MAX_EVENT_SIZE}.
     *
     * @param enableLargeEvents Enable or disables LargeEvent processing.
     * @return LargeEvent processing is enabled or disabled.
     */
    private final boolean enableLargeEvents;

    public static final class EventWriterConfigBuilder {
        private static final long MIN_TRANSACTION_TIMEOUT_TIME_MILLIS = 10000;
        private int initialBackoffMillis = 1;
        private int maxBackoffMillis = 20000;
        private int retryAttempts = 10;
        private int backoffMultiple = 10;
        private long transactionTimeoutTime = 600 * 1000 - 1;
        private boolean automaticallyNoteTime = false; 
        // connection pooling for event writers is disabled by default.
        private boolean enableConnectionPooling = false;
        private boolean enableLargeEvents = false;

        public EventWriterConfig build() {
            Preconditions.checkArgument(transactionTimeoutTime >= MIN_TRANSACTION_TIMEOUT_TIME_MILLIS, "Transaction time must be at least 10 seconds.");
            Preconditions.checkArgument(initialBackoffMillis >= 0, "Backoff times must be positive numbers");
            Preconditions.checkArgument(backoffMultiple >= 0, "Backoff multiple must be positive numbers");
            Preconditions.checkArgument(maxBackoffMillis >= 0, "Backoff times must be positive numbers");
            Preconditions.checkArgument(retryAttempts >= 0, "Retry attempts must be a positive number");
            return new EventWriterConfig(initialBackoffMillis, maxBackoffMillis, retryAttempts, backoffMultiple,
                                         enableConnectionPooling,
                                         transactionTimeoutTime,
                                         automaticallyNoteTime,
                                         enableLargeEvents);
        }
    }
}
