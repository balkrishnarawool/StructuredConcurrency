package com.balarawool.loom.util;

import java.util.List;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class GamesUtil {

    public static Player getPlayer() {
        logAndWait("getPlayer");
        return new Player("Virat Kohli", new Performance(List.of(new Score(87), new Score(97), new Score(107), new Score(37), new Score(127))));
    }

    public record Player(String name, Performance performance) {}
    public record Performance(List<Score> scores) {}
    public record Score(int runs) {}
}