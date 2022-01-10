package calculate;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class DrawEdgeTask extends Task<ArrayList<Edge>> implements Observer {
    private KochFractalGenerator kochFractalGenerator;
    private Generate generate;
    private ArrayList<Edge> edges;

    DrawEdgeTask(Generate generate, int nxt) {
        this.generate = generate;
        kochFractalGenerator = new KochFractalGenerator(this);
        edges = new ArrayList<>();
        kochFractalGenerator.setLevel(nxt);
        kochFractalGenerator.addObserver(this);
    }

    @Override
    protected void cancelled() {
        super.cancel();
        kochFractalGenerator.cancel();
    }

    @Override
    protected ArrayList<Edge> call() {
        switch (generate) {
            case RIGHT -> kochFractalGenerator.rightEdge();
            case BOTTOM -> kochFractalGenerator.bottomEdge();
            case LEFT -> kochFractalGenerator.leftEdge();
        }
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
        updateProgress(edges.size(), kochFractalGenerator.getNumberOfEdges() / 3);
        updateMessage("" + edges.size());
    }
}
