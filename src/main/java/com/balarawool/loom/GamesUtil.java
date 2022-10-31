package com.balarawool.loom;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GamesUtil {

    public static Player getPlayer() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Player("Virat Kohli", new Performance(List.of(new Score(87), new Score(97), new Score(107), new Score(37), new Score(127))));
    }

    public record Player(String name, Performance performance) {}
    public record Performance(List<Score> scores) {}
    public record Score(int runs) {}
}