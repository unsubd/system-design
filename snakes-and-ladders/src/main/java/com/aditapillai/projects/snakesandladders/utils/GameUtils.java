package com.aditapillai.projects.snakesandladders.utils;

import com.aditapillai.projects.snakesandladders.actors.Player;
import com.aditapillai.projects.snakesandladders.actors.Snake;
import com.aditapillai.projects.snakesandladders.constants.Color;
import com.aditapillai.projects.snakesandladders.constants.Status;
import com.aditapillai.projects.snakesandladders.core.Board;
import com.aditapillai.projects.snakesandladders.core.Game;
import com.aditapillai.projects.snakesandladders.models.Ladder;
import com.aditapillai.projects.snakesandladders.models.Square;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class GameUtils {

    public static Game createStaticGame() {
        Set<Ladder> ladders = new HashSet<>();
        ladders.add(new Ladder(10, 22));
        ladders.add(new Ladder(32, 85));
        ladders.add(new Ladder(43, 45));

        Set<Snake> snakes = new HashSet<>();
        snakes.add(new Snake(98, 3));
        snakes.add(new Snake(52, 30));
        snakes.add(new Snake(60, 9));
        snakes.add(new Snake(77, 25));

        Square[] squares = IntStream.rangeClosed(0, 100)
                                    .mapToObj(index -> new Square())
                                    .toArray(Square[]::new);

        snakes.forEach(snake -> squares[snake.getMouth()].setSnake(snake));
        ladders.forEach(ladder -> squares[ladder.getStart()].setLadder(ladder));

        Board board = new Board(squares);

        Set<Player> players = new HashSet<>();
        players.add(new Player("Player 1", Color.RED));
        players.add(new Player("Player 2", Color.YELLOW));
        players.add(new Player("Player 3", Color.GREEN));
        players.add(new Player("Player 4", Color.BLUE));

        Game game = Game.builder()
                        .board(board)
                        .players(players)
                        .status(Status.CREATED)
                        .build();

        return game;
    }

    public Game createGame(Set<Snake> snakes, Set<Ladder> ladders) {
        Square[] squares = IntStream.rangeClosed(0, 100)
                                    .mapToObj(index -> new Square())
                                    .toArray(Square[]::new);

        snakes.forEach(snake -> squares[snake.getMouth()].setSnake(snake));
        ladders.forEach(ladder -> squares[ladder.getStart()].setLadder(ladder));

        Board board = new Board(squares);

        Set<Player> players = new HashSet<>();
        players.add(new Player("Player 1", Color.RED));
        players.add(new Player("Player 2", Color.YELLOW));
        players.add(new Player("Player 3", Color.GREEN));
        players.add(new Player("Player 4", Color.BLUE));

        Game game = Game.builder()
                        .board(board)
                        .players(players)
                        .status(Status.CREATED)
                        .build();

        return game;
    }
}
