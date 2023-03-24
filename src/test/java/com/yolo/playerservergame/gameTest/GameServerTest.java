package com.yolo.playerservergame.gameTest;

//import org.junit.jupiter.api.Test;
//import org.springframework.web.socket.client.WebSocketClient;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * JUnit test case for the testing the Return-to-Player (RTP)
// * % of a game server over a WebSocket connection
// * */
//
//public class GameServerTest {
//
//    //WebSocket endpoint on the server side
//    private static final String WS_URI = "ws://localhost:8080/game";
//
//    /**
//     * Sets up a WebSocket client to connect to the game
//     * server at the URI 'ws:localhost:8080/game'
//     * */
//    @Test
//    public void testRTP() throws URISyntaxException, InterruptedException {
//        //creating 24 threads
//        final int numThreads = 24;
//        //1000000 rounds / 24 threads = 41667
//        final int roundsPerThread = 41667;
//
//        // Set up WebSocket client for game
//        final WebSocketClient client = new WebSocketClient(new URI(WS_URI));
//
//        // Create countdown latch to wait for all game rounds to complete
//        final CountDownLatch latch = new CountDownLatch(numThreads * roundsPerThread);
//
//        // Set up thread pool to run game rounds in parallel
//        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
//        // Run game rounds in parallel threads
//        for (int i = 0; i < numThreads; i++) {
//            executorService.execute(() -> {
//                for (int j = 0; j < roundsPerThread; j++) {
//                    try {
//                        double bet = 1.0;
//                        int playerNumber = (int) (Math.random() * 100) + 1;
//                        client.send(String.format("%.2f,%d", bet, playerNumber));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        latch.countDown();
//                    }
//                }
//            });
//        }
//
//        // Wait for all game rounds to complete
//        latch.await();
//
//        // Close WebSocket client and thread pool
//        client.close();
//        executorService.shutdown();
//
//        // Calculate RTP based on total spend and total win
//        double totalSpend = numThreads * roundsPerThread;
//        double totalWin = client.getTotalWin();
//        double rtp = (totalWin / totalSpend) * 100;
//
//        // Verify RTP is within 1% of expected value
//        assertEquals(99.0, rtp, 1.0);
//    }
//}
import com.yolo.playerservergame.gameServer.GameServer;

import com.yolo.playerservergame.player.Player;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Responsible for simulating of playing one
 * million rounds in parallel across 24 threads
 * and calculating Return-to-Player value based on
 * the total amount bet and total amount won.
 * */

public class GameServerTest {

    /**
     * using JUnit test runner
     * @throws InterruptedException will run if testRTP is interrupted
     * while waiting for operation to complete or if timout occurs
     * */
    @Test
    public void testRTP() throws InterruptedException {
        /*
        * ExecutorService creates a thread pool of 24 threads,
        * this will allow test to execute one million rounds
        * in parallel 24 threads.
        * */
        int NUM_THREADS = 24;
        int NUM_ROUNDS = 1000000;
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        //used to keep track of total amount of bet and won over one million rounds
        double totalBet = 0;
        /*
        * declares an array of doubles with a single element initialized to 0,
        * the use of [] allows to modify the value of 'totalWin' inside lambda,
        * 'final' ensures that reference of [] cannot be reassigned but value
        * of its elements can be modified.
        * totalWin used to accumulate total amount won.
        * */
        final double[] totalWin = {0};

        /*
        * for loop simulates one million rounds,
        * each iteration of the loop generates a random bet of 1.0,
        * and random player number between 1 and 100
        * */
        for (int i = 0; i < NUM_ROUNDS; i++) {
            double bet = 1.0;
            int playerNumber = (int) (Math.random() * 100) + 1;
            //updating current bet
            totalBet += bet;
            //creating Player object
            Player player = new Player(bet, playerNumber);
            //creating GameServer object
            GameServer game = new GameServer();
            /*
            * lambda - totalWin single element is passed to
            * 'ExecutorService' that runs game simulation in
            * separate threads.
            * new instances of player and game are submitted to
            * */
            executor.execute(() -> {
                try {
                    //converting values into string and assigning them to winStr
                    String winStr = String.valueOf(game.calculateWin(player.getBet(), player.getPlayerNumber(), player.generateServerNumber()));
                    //converting winStr into double value win
                    double win = Double.parseDouble(winStr);
                    //adding win to totalWin []
                    totalWin[0] += win;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        /*
        * after one million rounds have been submitted to the thread pool,
        * test shuts down the executor and waits for all threads to
        * finish executing
        * */
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        /*
        * after threads finished, test calculates Return-To-Player
        * by dividing total win by the total bet and * by 100
        * */
        double rtp = totalWin[0] / totalBet * 100;
        //setting expected RTP value to 99.0
        double expectedRTP = 99.0;

        /*
        * comparing expected RTP with actual RTP
        * */
        assertEquals(expectedRTP, rtp, 0.1);
    }
}

