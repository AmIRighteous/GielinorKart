package com.gielinorkart;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.List;


@Getter
public class Race {
    private String courseName;
    private WorldPoint start;
    private WorldPoint end;
    private List<WorldPoint> checkpoints;

    public Race(String courseName, WorldPoint start, WorldPoint end, List<WorldPoint> checkpoints) {
        this.courseName = courseName;
        this.start = start;
        this.end = end;
        this.checkpoints = checkpoints;
    }
}
