package ntu.mdp.pathfinding;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ntu.mdp.pathfinding.Algo.AlgoConstant;
import ntu.mdp.pathfinding.Algo.ShortestPathAlgo;
import ntu.mdp.pathfinding.Algo.ShortestPathTrajectory;
import ntu.mdp.pathfinding.Algo.ShortestPathTrajectoryResult;
import ntu.mdp.pathfinding.GUI.Grid;
import ntu.mdp.pathfinding.GUI.GridControlPane;
import ntu.mdp.pathfinding.GUI.ImageRecognizePane;
import ntu.mdp.pathfinding.GUI.SimulatorConstant;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


class ShortestPathRunner implements Runnable {

    @Override
    public void run() {
        Car car = InputData.getCar();
        ShortestPathTrajectory algo = new ShortestPathTrajectory(AlgoConstant.GridM, AlgoConstant.GridN, InputData.getObstacles(), InputData.getStartR(), InputData.getStartC());
        algo.findShortestValidPath();
//        ShortestPathAlgo algo = new ShortestPathAlgo(AlgoConstant.GridM, AlgoConstant.GridN, InputData.getObstacles(), InputData.getStartR(), InputData.getStartC());
//        ShortestPathTrajectoryResult result = algo.findShortestPath();
//        if (result == null)
//            System.out.println("No solution found");
//        else {
//            List<int[]> path = result.getPathGrids();
//            for (int[] p : path) {
//                car.goTo(p[0], p[1]);
//            }
//        }
        System.out.println("Path checking done");
        List<int[]> points = algo.getPathGrids();
        if (points == null || points.isEmpty()) {
            System.out.println("No solution found");
            return;
        }
        for (int[] p : points) {
            car.goTo(p[0], p[1]);
        }
    }
}

public class SimulatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Pane root = new Pane();
            URL url = getClass().getResource("assets");

            Grid grid = new Grid( SimulatorConstant.nRowGridGrid, SimulatorConstant.nColumnGrid,
                    SimulatorConstant.cellWidth, SimulatorConstant.cellHeight, SimulatorConstant.marginX,
                    SimulatorConstant.marginY, url);

            double startX = SimulatorConstant.marginX + SimulatorConstant.nColumnGrid * SimulatorConstant.cellWidth +
                    SimulatorConstant.gridIRPGap;
            double startY = SimulatorConstant.marginY;
            ImageRecognizePane irp = new ImageRecognizePane(startX, startY, SimulatorConstant.IRPWidth,
                    SimulatorConstant.IRPHeight, url);

            startY +=  SimulatorConstant.IRPHeight + 60;
            GridControlPane gControlP = new GridControlPane(startX, startY, SimulatorConstant.GControlPWidth,
                    SimulatorConstant.GControlPHeight, SimulatorConstant.GControlPGap, grid);

            root.getChildren().addAll(grid, irp, gControlP);

            double width = startX + SimulatorConstant.IRPWidth + SimulatorConstant.marginX;
            double height = 2 * SimulatorConstant.marginY + SimulatorConstant.nRowGridGrid * SimulatorConstant.cellHeight;

            // create scene and stage
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("cell.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new ShortestPathRunner());
        thread.start();
        launch();
    }
}