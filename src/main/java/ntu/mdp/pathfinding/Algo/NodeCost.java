package ntu.mdp.pathfinding.Algo;

public class NodeCost {
    public static int distanceOfObstacles(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }
}