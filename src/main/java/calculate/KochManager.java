/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import timeutil.TimeStamp;
import ui.Fun3KochFractalFx;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    private ArrayList<Edge> edges;
    private DrawEdgeTask taskLeft;
    private DrawEdgeTask taskRight;
    private DrawEdgeTask taskBottom;
    private Fun3KochFractalFx application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    private ExecutorService pool;

    public KochManager(Fun3KochFractalFx application) {
        this.application = application;
        this.edges = new ArrayList<>();
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
        this.pool = Executors.newFixedThreadPool(3);
    }

    public void changeLevel(int nxt) {
        if (taskLeft != null) {
            taskLeft.cancel();
        }
        if (taskRight != null) {
            taskRight.cancel();
        }
        if (taskBottom != null) {
            taskBottom.cancel();
        }

        edges.clear();

        taskLeft = new DrawEdgeTask(Generate.LEFT, nxt);
        taskRight = new DrawEdgeTask(Generate.RIGHT, nxt);
        taskBottom = new DrawEdgeTask(Generate.BOTTOM, nxt);

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

        pool.execute(() -> {
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
        });
    }

    public void drawEdges() {
        application.setLblCalcText(tsCalc.toString());
        application.setLblNumberOfEdgesText("" + (edges.size()));

        System.out.println("Draw!");

        tsDraw.init();
        tsDraw.setBegin("Begin");

        application.clearKochPanel();

        for (Edge e : edges) {
            application.drawEdge(e);
        }

        tsDraw.setEnd("End");

        application.setLblDrawText(tsDraw.toString());
    }

    public void stopPool() {
        pool.shutdown();
    }
}
