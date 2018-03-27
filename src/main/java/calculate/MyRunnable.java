package calculate;

import java.util.ArrayList;

public class MyRunnable implements Runnable{
    private KochFractal kochFractal;
    private Generate generate;
    private ArrayList<Edge> edges;
    private SharedCount sharedCount;
    MyRunnable(Generate generate, int nxt, SharedCount s){
        this.generate = generate;
        kochFractal = new KochFractal(this);
        edges = new ArrayList<>();
        kochFractal.setLevel(nxt);
        this.sharedCount = s;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    @Override
    public void run() {
        switch (generate){
            case RIGHT:
                kochFractal.generateRightEdge();
                break;
            case BOTTOM:
                kochFractal.generateBottomEdge();
                break;
            case LEFT:
                kochFractal.generateLeftEdge();
                break;
        }
        sharedCount.increase();
    }
    public void addEdge(Edge e){
        this.edges.add(e);
    }
}
