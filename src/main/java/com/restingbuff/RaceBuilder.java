package com.restingbuff;

import net.runelite.api.coords.WorldPoint;
import java.util.Collections;
import java.util.List;

public class RaceBuilder {

    private WorldPoint _start;
    private WorldPoint _end;
    private List<WorldPoint> _checkpoints = Collections.emptyList();

    public RaceBuilder() {}

    public Race buildRace() {
        return new Race(_start, _end, _checkpoints);
    }
    public RaceBuilder start(WorldPoint start) {
        this._start = start;
        return this;
    }

    public RaceBuilder end(WorldPoint end) {
        this._end = end;
        return this;
    }

    public RaceBuilder checkpoint(List<WorldPoint> checkpoints) {
        this._checkpoints = checkpoints;
        return this;
    }
}
