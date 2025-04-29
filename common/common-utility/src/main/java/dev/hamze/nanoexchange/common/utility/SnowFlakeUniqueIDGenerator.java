package dev.hamze.nanoexchange.common.utility;

import lombok.Getter;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public final class SnowFlakeUniqueIDGenerator {

    private static final Map<Long, SnowFlakeUniqueIDGenerator> ID_GENERATOR_MAP = new ConcurrentHashMap<>();
    private final SnowFlakeConfig config;
    private final long nodeId;
    private final AtomicLong sequence = new AtomicLong(0L);
    private volatile long lastTimestamp = -1L;

    private SnowFlakeUniqueIDGenerator(long nodeId, SnowFlakeConfig config) {
        if (nodeId < 0 || nodeId > config.getMaxNodeId()) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, config.getMaxNodeId()));
        }
        this.nodeId = nodeId;
        this.config = config;
    }

    public static SnowFlakeUniqueIDGenerator getGenerator(long nodeId) {
        return ID_GENERATOR_MAP.computeIfAbsent(nodeId, k -> new SnowFlakeUniqueIDGenerator(nodeId, SnowFlakeConfig.DEFAULT_CONFIG));
    }

    public static long nextId(long nodeId) {
        return getGenerator(nodeId).nextId();
    }

    public static SnowFlakeUniqueID parse(long nodeId, long uniqueId) {
        return getGenerator(nodeId).parse(uniqueId);
    }

    public long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new ClockException("Invalid System Clock!");
        }

        long seq;
        if (currentTimestamp == lastTimestamp) {
            seq = sequence.incrementAndGet() & config.getMaxSequence();
            if (seq == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence.set(0L);
            seq = 0L;
        }

        lastTimestamp = currentTimestamp;

        return (currentTimestamp << (config.getNodeIdBits() + config.getSequenceBits()))
                | (nodeId << config.getSequenceBits())
                | seq;
    }

    public SnowFlakeUniqueID parse(long uniqueId) {
        long timestamp = (uniqueId >> (config.getNodeIdBits() + config.getSequenceBits())) + config.getCustomEpoch();
        long nodeId = (uniqueId >> config.getSequenceBits()) & config.getMaxNodeId();
        long sequence = uniqueId & config.getMaxSequence();
        return new SnowFlakeUniqueID(new Date(timestamp), nodeId, sequence);
    }

    private long timestamp() {
        return System.currentTimeMillis() - config.getCustomEpoch();
    }

    private long waitNextMillis(long currentTimestamp) {
        long nextTimestamp = timestamp();
        while (nextTimestamp <= currentTimestamp) {
            nextTimestamp = timestamp();
        }
        return nextTimestamp;
    }

    @Getter
    public static class SnowFlakeConfig {
        public static final SnowFlakeConfig DEFAULT_CONFIG = new SnowFlakeConfig(41, 10, 12, 1420070400000L);

        private final int epochBits;
        private final int nodeIdBits;
        private final int sequenceBits;
        private final long customEpoch;
        private final long maxNodeId;
        private final long maxSequence;

        public SnowFlakeConfig(int epochBits, int nodeIdBits, int sequenceBits, long customEpoch) {
            this.epochBits = epochBits;
            this.nodeIdBits = nodeIdBits;
            this.sequenceBits = sequenceBits;
            this.customEpoch = customEpoch;
            this.maxNodeId = (1L << nodeIdBits) - 1;
            this.maxSequence = (1L << sequenceBits) - 1;
        }

    }

    public record SnowFlakeUniqueID(Date timestamp, long nodeId, long sequence) {
    }

    public static class ClockException extends RuntimeException {
        public ClockException(String message) {
            super(message);
        }
    }
}