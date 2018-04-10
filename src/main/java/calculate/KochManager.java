/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;
import java.util.ArrayList;
import java.util.concurrent.*;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    private ArrayList<Edge> edges;
    private MyTask taskLeft;
    private MyTask taskRight;
    private MyTask taskBottom;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    private ExecutorService pool;
    
    public KochManager(FUN3KochFractalFX application) {
        this.application = application;
        this.edges = new ArrayList<>();
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
        pool = Executors.newFixedThreadPool(3);
    }

    public void changeLevel(int nxt) {
        if(taskLeft != null){
            taskLeft.cancel();
        }
        if(taskRight != null){
            taskRight.cancel();
        }
        if(taskBottom != null){
            taskBottom.cancel();
        }
        edges.clear();
        taskLeft = new MyTask(Generate.LEFT, nxt);
        taskRight = new MyTask(Generate.RIGHT, nxt);
        taskBottom = new MyTask(Generate.BOTTOM, nxt);

        application.getProgLeft().progressProperty().bind(taskLeft.progressProperty());
        application.getProgRight().progressProperty().bind(taskRight.progressProperty());
        application.getProgBottom().progressProperty().bind(taskBottom.progressProperty());
        application.getLblProgressLeftText().textProperty().bind(taskLeft.messageProperty());
        application.getLblProgressRightText().textProperty().bind(taskRight.messageProperty());
        application.getLblProgressBottomText().textProperty().bind(taskBottom.messageProperty());

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
                    taskLeft = null;
                    taskRight = null;
                    taskBottom = null;
                    tsCalc.setEnd("End");
                    application.requestDrawEdges();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public void drawEdges() {
        application.setTextCalc(tsCalc.toString());
        application.setTextNrEdges("" + (edges.size()));
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
    public void stopPool(){
        pool.shutdown();
    }
}
