package calculate;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyTask extends Task<ArrayList<Edge>> implements Observer {
    private KochFractal kochFractal;
    private Generate generate;
    private ArrayList<Edge> edges;
    MyTask(Generate generate, int nxt){
        this.generate = generate;
        kochFractal = new KochFractal(this);
        edges = new ArrayList<>();
        kochFractal.setLevel(nxt);
        kochFractal.addObserver(this);
    }

    @Override
    protected ArrayList<Edge> call(){
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
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
        updateProgress(edges.size(), kochFractal.getNrOfEdges()/3);
        updateMessage("" + edges.size());
    }
}
