package com.aditapillai.projects.snakesandladders.actors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Snake {
    private final int mouth;
    private final int tail;

    public void bite(Player player) {
        player.setCurrentPosition(tail);
    }
}
