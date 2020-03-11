package com.aditapillai.projects.snakesandladders.models;

import com.aditapillai.projects.snakesandladders.actors.Player;
import com.aditapillai.projects.snakesandladders.actors.Snake;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Square {
    private final Set<Player> players = new HashSet<>();
    private Snake snake;
    private Ladder ladder;

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public boolean isOccupied() {
        return !this.players.isEmpty();
    }

    public Square clone() {
        Square square = new Square();
        square.setLadder(ladder);
        square.setSnake(snake);
        players.forEach(square::addPlayer);
        return square;
    }

    public Set<Player> getPlayers() {
        return new HashSet<>(this.players);
    }
}
