/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author kristof
 */
public class ThreadExecutor {

    private static ExecutorService executor;

    private ThreadExecutor() {

    }

    public static void execute(Runnable command) {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        executor.execute(command);
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

}
