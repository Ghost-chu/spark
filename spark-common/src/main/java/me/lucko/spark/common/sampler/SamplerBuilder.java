/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.common.sampler;

import java.util.concurrent.TimeUnit;

/**
 * Builds {@link Sampler} instances.
 */
public class SamplerBuilder {

    private double samplingInterval = 4; // milliseconds
    private boolean includeLineNumbers = false;
    private boolean ignoreSleeping = false;
    private long timeout = -1;
    private ThreadDumper threadDumper = ThreadDumper.ALL;
    private ThreadGrouper threadGrouper = ThreadGrouper.BY_NAME;

    private int ticksOver = -1;
    private TickCounter tickCounter = null;

    public SamplerBuilder() {
    }

    public SamplerBuilder samplingInterval(double samplingInterval) {
        this.samplingInterval = samplingInterval;
        return this;
    }

    public SamplerBuilder completeAfter(long timeout, TimeUnit unit) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout > 0");
        }
        this.timeout = System.currentTimeMillis() + unit.toMillis(timeout);
        return this;
    }

    public SamplerBuilder threadDumper(ThreadDumper threadDumper) {
        this.threadDumper = threadDumper;
        return this;
    }

    public SamplerBuilder threadGrouper(ThreadGrouper threadGrouper) {
        this.threadGrouper = threadGrouper;
        return this;
    }

    public SamplerBuilder ticksOver(int ticksOver, TickCounter tickCounter) {
        this.ticksOver = ticksOver;
        this.tickCounter = tickCounter;
        return this;
    }

    public SamplerBuilder includeLineNumbers(boolean includeLineNumbers) {
        this.includeLineNumbers = includeLineNumbers;
        return this;
    }

    public SamplerBuilder ignoreSleeping(boolean ignoreSleeping) {
        this.ignoreSleeping = ignoreSleeping;
        return this;
    }

    public Sampler start() {
        Sampler sampler;

        int intervalMicros = (int) (this.samplingInterval * 1000d);
        if (this.ticksOver == -1 || this.tickCounter == null) {
            sampler = new Sampler(intervalMicros, this.threadDumper, this.threadGrouper, this.timeout, this.includeLineNumbers, this.ignoreSleeping);
        } else {
            sampler = new Sampler(intervalMicros, this.threadDumper, this.threadGrouper, this.timeout, this.includeLineNumbers, this.ignoreSleeping, this.tickCounter, this.ticksOver);
        }

        sampler.start();
        return sampler;
    }

}
