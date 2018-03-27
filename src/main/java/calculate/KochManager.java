/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    private List<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    private Thread t1;
    private Thread t2;
    private Thread t3;
    
    public KochManager(FUN3KochFractalFX application) {
        this.application = application;
        this.edges = Collections.synchronizedList(new ArrayList<>());
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }
    public void changeLevel(int nxt) {
        edges.clear();
        SharedCount sharedCount = new SharedCount();
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");
        MyRunnable mr1 = new MyRunnable(Generate.RIGHT, nxt, sharedCount);
        MyRunnable mr2 = new MyRunnable(Generate.BOTTOM, nxt, sharedCount);
        MyRunnable mr3 = new MyRunnable(Generate.LEFT, nxt, sharedCount);
        t1 = new Thread(mr1);
        t2 = new Thread(mr2);
        t3 = new Thread(mr3);
        t1.start();
        t2.start();
        t3.start();
        while(sharedCount.getCount() < 3){

        }
        tsCalc.setEnd("End calculating");
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        edges.addAll(mr1.getEdges());
        edges.addAll(mr2.getEdges());
        edges.addAll(mr3.getEdges());
        application.requestDrawEdges();
        application.setTextNrEdges("" + edges.size());
        application.setTextCalc(tsCalc.toString());
    }
    
    public void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }
}
