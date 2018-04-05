package calculate;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyTask extends Task<ArrayList<Edge>> implements Observer {
    private KechFractal kechFractal;
    private Generate generate;
    private ArrayList<Edge> edges;
    private SharedCount sharedCount;
    MyTask(Generate generate, int nxt, SharedCount s){
        this.generate = generate;
        kechFractal = new KechFractal(this);
        edges = new ArrayList<>();
        kechFractal.setLevel(nxt);
        this.sharedCount = s;
        kechFractal.addObserver(this);
    }

    @Override
    protected ArrayList<Edge> call(){
        switch (generate){
            case RIGHT:
                kechFractal.generateRightEdge();
                break;
            case BOTTOM:
                kechFractal.generateBottomEdge();
                break;
            case LEFT:
                kechFractal.generateLeftEdge();
                break;
        }
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
        updateProgress(edges.size(), kechFractal.getNrOfEdges()/3);
        updateMessage("" + edges.size());
    }
}
