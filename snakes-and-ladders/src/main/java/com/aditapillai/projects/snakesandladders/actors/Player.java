package com.aditapillai.projects.snakesandladders.actors;

import com.aditapillai.projects.snakesandladders.constants.Color;
import com.aditapillai.projects.snakesandladders.models.Ladder;
import com.aditapillai.projects.snakesandladders.utils.Dice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Color color;
    private boolean active;

    @Setter
    private int currentPosition;

    public void climb(Ladder ladder) {
        this.currentPosition = ladder.getEnd();
    }

    public int rollDice() {
        return Dice.roll();
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return color == player.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return String.format("(%s,%d)", this.color, this.currentPosition);
    }

    public Player clone() {
        Player player = new Player(name, this.color);
        player.setActive(this.active);
        return player;
    }
}
