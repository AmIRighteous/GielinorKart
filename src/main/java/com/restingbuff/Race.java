package com.restingbuff;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.List;


@Getter
public class Race {
    private WorldPoint start;
    private WorldPoint end;
    private List<WorldPoint> checkpoints;

    public Race(WorldPoint start, WorldPoint end, List<WorldPoint> checkpoints) {
        this.start = start;
        this.end = end;
        this.checkpoints = checkpoints;
    }
}
