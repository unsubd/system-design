package com.aditapillai.projects.snakesandladders.core;

import com.aditapillai.projects.snakesandladders.actors.Player;
import com.aditapillai.projects.snakesandladders.actors.Snake;
import com.aditapillai.projects.snakesandladders.constants.Color;
import com.aditapillai.projects.snakesandladders.models.Ladder;
import com.aditapillai.projects.snakesandladders.models.Square;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class BoardTest {

    private Board board;
    private Square[] squares;

    @Before
    public void setUp() {
        squares = IntStream.rangeClosed(0, 100)
                           .mapToObj(index -> new Square())
                           .toArray(Square[]::new);
        this.board = new Board(squares);
    }

    @Test
    public void place() {
        Player player = new Player("Test player", Color.GREEN);
        int finalPosition = board.place(player, 4);
        Assert.assertEquals(4, finalPosition);
    }

    @Test
    public void doNotMove() {
        Player player = new Player("Test player", Color.GREEN);
        this.squares[98].addPlayer(player);
        player.setCurrentPosition(98);
        int finalPosition = board.place(player, 4);
        Assert.assertEquals(98, finalPosition);
    }

    @Test
    public void placeAndValidateSquare() {
        Player player = new Player("Test Player", Color.RED);
        player.setCurrentPosition(56);
        this.squares[56].addPlayer(player);
        int finalPosition = this.board.place(player, 10);

        Assert.assertEquals(66, finalPosition);
        Assert.assertTrue(this.squares[66].isOccupied());
        Assert.assertEquals(player, this.squares[66].getPlayers()
                                                    .stream()
                                                    .findFirst()
                                                    .orElse(null));
        Assert.assertFalse(this.squares[56].isOccupied());
    }

    @Test
    public void snakeBite() {
        Snake snake = new Snake(99, 5);
        this.squares[99].setSnake(snake);
        Player player = new Player("Player X", Color.GREEN);
        player.setCurrentPosition(96);
        int roll = 3;
        int finalPosition = this.board.place(player, roll);

        Assert.assertEquals(5, finalPosition);
    }

    @Test
    public void ladderClimb() {
        Ladder ladder = new Ladder(15, 47);
        this.squares[15].setLadder(ladder);
        Player player = new Player("Test Player", Color.GREEN);
        player.setCurrentPosition(10);
        int roll = 5;
        int finalPosition = this.board.place(player, roll);
        Assert.assertEquals(47, finalPosition);
    }

    @Test
    public void isLastOccupied() {
        Assert.assertFalse(this.board.isLastOccupied());
        squares[100].addPlayer(new Player("Test Player", Color.RED));
        Assert.assertTrue(this.board.isLastOccupied());
    }

    @Test
    public void getWinner() {
        Player test_player = new Player("Test Player", Color.RED);
        this.squares[100].addPlayer(test_player);
        Player winner = this.board.getWinner();
        Assert.assertEquals(winner, test_player);
    }

    @Test
    public void getCurrentBoardClone() {
        this.squares[16].addPlayer(new Player("Test Player", Color.YELLOW));
        Square[] currentBoardClone = this.board.getCurrentBoardClone();
        IntStream.rangeClosed(0, 100)
                 .forEach(index -> Assert.assertEquals(this.squares[index].getPlayers(), currentBoardClone[index].getPlayers()));
    }
}