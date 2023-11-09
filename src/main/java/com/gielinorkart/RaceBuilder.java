package com.gielinorkart;

import net.runelite.api.coords.WorldPoint;
import java.util.Collections;
import java.util.List;

public class RaceBuilder {

    private String _courseName;
    private WorldPoint _start;
    private WorldPoint _end;
    private List<WorldPoint> _checkpoints = Collections.emptyList();

    public RaceBuilder() {}

    public Race buildRace() {
        return new Race(_courseName, _start, _end, _checkpoints);
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

    public RaceBuilder courseName(String courseName) {
        this._courseName = courseName;
        return this;
    }
}
