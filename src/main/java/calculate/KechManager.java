/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;
import java.util.ArrayList;
import java.util.concurrent.*;

import fun3kochfractalfx.FUN3KechFractalFX;
import javafx.application.Platform;
import javafx.concurrent.Task;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KechManager {
    private ArrayList<Edge> edges;
    private MyTask taskLeft;
    private MyTask taskRight;
    private MyTask taskBottom;
    private FUN3KechFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    
    public KechManager(FUN3KechFractalFX application) {
        this.application = application;
        this.edges = new ArrayList<>();
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }

    public void changeLevel(int nxt) {
        edges.clear();
        SharedCount sharedCount = new SharedCount();
        taskLeft = new MyTask(Generate.LEFT, nxt, sharedCount);
        taskRight = new MyTask(Generate.RIGHT, nxt, sharedCount);
        taskBottom = new MyTask(Generate.BOTTOM, nxt, sharedCount);
        application.getProgLeft().progressProperty().bind(taskLeft.progressProperty());
        application.getProgRight().progressProperty().bind(taskRight.progressProperty());
        application.getProgBottom().progressProperty().bind(taskBottom.progressProperty());
        application.getLblProgressLeftText().textProperty().bind(taskLeft.messageProperty());
        application.getLblProgressRightText().textProperty().bind(taskRight.messageProperty());
        application.getLblProgressBottomText().textProperty().bind(taskBottom.messageProperty());
        ExecutorService pool = Executors.newFixedThreadPool(3);
        tsCalc.init();
        tsCalc.setBegin("Begin");
        pool.submit(taskLeft);
        pool.submit(taskRight);
        pool.submit(taskBottom);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    edges.addAll(taskLeft.get());
                    edges.addAll(taskRight.get());
                    edges.addAll(taskBottom.get());
                    tsCalc.setEnd("End");
                    application.requestDrawEdges();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            application.setTextNrEdges("" + (edges.size()));
                            application.setTextCalc(tsCalc.toString());
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        pool.shutdown();9
    }
    
    public void drawEdges() {
        System.out.println("Draw!");
        tsDraw.init();
        tsDraw.setBegin("Begin");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End");
        application.setTextDraw(tsDraw.toString());
    }
}
