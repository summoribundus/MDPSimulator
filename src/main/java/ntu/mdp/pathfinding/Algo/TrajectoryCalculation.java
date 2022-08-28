package ntu.mdp.pathfinding.Algo;

import java.util.HashMap;

/***
 * this is the trajectory calculation from one obstacle to another - for simulation
 */
public class TrajectoryCalculation {

    // the constant r, HARDCODED now
    final int r = 23;
    // opposite direction mapping
    final HashMap<Integer, Integer> oppositeDirMap = new HashMap<Integer, Integer>() {{put(0, 2); put(1, 3); put(2, 0); put(3, 1);}};

    // the real coordinates of the obstacle
    private int targetC;
    private int targetR;
    private int targetTheta;
    private int obstacleDir;

    // the real coordinates of the robot
    private int robotC;
    private int robotR;
    private int robotTheta;


    public TrajectoryCalculation(int obsC, int obsR, int obsDir, int robotC, int robotR, int robotTheta){
        this.targetC = obsC;
        this.targetR = obsR;
        this.obstacleDir = obsDir; // of value [0, 1, 2, 3]
        this.targetTheta = (int)(oppositeDirMap.get(obsDir) * Math.PI / 2); // represent the data in PI.
        this.robotC = robotC;
        this.robotR = robotR;
        this.robotTheta = robotTheta;
    }

    // to calculate the position of the circle
    public int[] calculateRobotStartingCenterOfCircle(int robotC, int robotR, int robot_theta, int targetC){
        // dir denotes left or right turning. 0 -> left turn, 1 -> right turn
        // turn left, to find circle point, clockwise. turn right, counterclockwise.
        double circlePointC = 0;
        double circlePointR = 0;

        // the unit vector v1 in direction of the robot.
        double v1C = Math.cos(robot_theta);
        double v1R = Math.sin(robot_theta);

        // find v1 counterclockwise/clockwise rotate to v2 according to the directions
        double v2C = 0;
        double v2R = 0;
        if (targetC > robotC) //turning left, counterclockwise
        {
            v2C = -v1R;
            v2R = v1C;
        } else {
            v2C = v1R;
            v2R = -v1C;
        }

        // find the turning circle point.
        circlePointC = robotC + v2C * r;
        circlePointR = robotR + v2R * r;

        return new int[]{(int)circlePointC, (int)circlePointR};
    }

    // calculate the obstacle circle center
    public int[] calculateObstacleCircleCenter(int targetC, int targetR, int robotC, int robotR, int obstacleDir){
        // turning right
        if (targetC > robotC) {
            if (obstacleDir == 1 || obstacleDir == 3)
                return new int[]{targetC - r, targetR};
            else if (targetR > robotR) // upper right
                return new int[]{targetC, targetR - r};
            else
                return new int[]{targetC, targetR + r};
        }
        // turning left
        else {
            if (obstacleDir == 1 || obstacleDir == 3)
                return new int[]{targetC + r, targetR};
            else if (targetR > robotR) // upper left
                return new int[]{targetC, targetR - r};
            else
                return new int[]{targetC, targetR + r};
        }
    }



    // function to calculate euclidean distance of 2 functions
    public int calculateEuclideanDistance(int c1, int r1, int c2, int r2){
        int deltaC = Math.abs(c1-c2);
        int deltaR = Math.abs(r1-r2);
        return (int)Math.sqrt(Math.pow(deltaC, 2) + Math.pow(deltaR, 2));
    }

    // arc angle computation
    public double calculateArcAngle(int startC, int startR, int destC, int destR, int dir) {
        double alpha = Math.atan2(destR, destC) - Math.atan2(startR, startC);
        if (alpha < 0 && dir == 0)
            alpha = alpha + 2*Math.PI;
        else if (alpha > 0 && dir == 1)
            alpha = alpha - 2*Math.PI;

        return Math.abs(alpha);
    }


    // arc length computation
    public int calculateArcLength(int startC, int startR, int destC, int destR, int dir){
        // dir = 0 for turn left, direct = 1 for turn right
        // tan_x and tan_y are the coordinates for the starting point on the arc,
        // while destC and destR are the coordinates for the destination point on the arc
        double alpha = calculateArcAngle(startC, startR, destC, destR, dir);

        return (int)Math.abs(alpha * r);

    }

    // choose the path according to the relative position of
    public TrajectoryResult trajectoryResult() {

        int[] centerOfCircle = calculateRobotStartingCenterOfCircle(robotC, robotR, robotTheta, targetC); //calculate the center of the robot turning trajectory
        int robotCircleR = centerOfCircle[0];
        int robotCircleC =centerOfCircle[1];

        int[] obstacleCircle = calculateObstacleCircleCenter(targetC, targetR, robotC, robotR, obstacleDir);
        int obsCircleR = obstacleCircle[0];
        int obsCircleC = obstacleCircle[1];

        // 1. obstacle is at the right of the robot
        if (targetC > robotC) {

            // 1(a) if obstacle is in the upper right corner of the robot.
            if (targetR > robotR) {
                if (obstacleDir == 0 || obstacleDir == 1)
                    // rsr
                    return calculateRSR(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
                else
                    // rsl
                    return calculateRSL(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
            }
            // 1(b) if obstacle is in the lower right corner of the robot.
            else {
                if (obstacleDir == 1 || obstacleDir == 2)
                    // rsr
                    return calculateRSR(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
                else
                    // rsl
                    return calculateRSL(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
            }

        }
        // 2. obstacle is at the left of the robot
        else {

            // 2(a) if obstacle is at the upper left of the robot
            if (targetR > robotR){
                //lsl
                if (obstacleDir == 1 || obstacleDir == 2)
                    return calculateLSL(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
                    //lsr
                else
                    return calculateLSR(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);

            }
            // 2(b) if obstacle is at the lower left of the robot
            else {
                //lsl
                if (obstacleDir == 0 || obstacleDir == 1)
                    return calculateLSL(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);
                    //lsr
                else
                    return calculateLSR(robotC, robotR, targetC, targetR, robotCircleR,
                            robotCircleC, obsCircleR, obsCircleC);

            }
        }
    }

    // calculate rsr type path.
    public TrajectoryResult calculateRSR(int robotR, int robotC,
                                         int targetR, int targetC,
                                         int robotCircleR, int robotCircleC,
                                         int obsCircleR, int obsCircleC){

        int l = calculateEuclideanDistance(obsCircleR, obsCircleC, robotCircleR, robotCircleC);
        // calculate v1 which points from center p1 to center p2
        double v1R = obsCircleR - robotCircleR;
        double v1C = obsCircleC - robotCircleC;
        // v2 is rotated by pi/2
        double v2R = -v1C;
        double v2C = v1R;

        // get the intermidiate pt1 position
        int pt1R = robotCircleR + (int)(v2R * r/l);
        int pt1C = robotCircleC + (int)(v2C * r/l);

        // get the intermidiate pt2 position
        int pt2R = pt1R + (int)v1R;
        int pt2C = pt1C + (int)v1C;

        // calculate the 2 arcs
        int p1pR = robotR - robotCircleR;
        int p1pC = robotC - robotCircleC;
        int p1pt1R = pt1R - robotCircleR;
        int p1pt1C = pt1C - robotCircleC;

        int alpha1 = (int)calculateArcAngle(p1pR, p1pC, p1pt1R, p1pt1C, 1);
        int arc1 = calculateArcLength(p1pR, p1pC, p1pt1R, p1pt1C, 1);

        int p2pt2R = pt2R - obsCircleR;
        int p2pt2C = pt2C - obsCircleC;
        int p2pR = targetR - obsCircleR;
        int p2pC = targetC - obsCircleC;

        int alpha2 = (int)calculateArcAngle(p2pt2R, p2pt2C, p2pR, p2pC, 1);
        int arc2 = calculateArcLength(p2pt2R, p2pt2C, p2pR, p2pC, 1);

        int totalLength = arc1 + arc2 + l;

        TrajectoryResult res = new TrajectoryResult(new int[]{pt1R, pt1C}, new int[]{pt2R, pt2C}, new int[]{robotCircleR, robotCircleC},
                new int[]{obsCircleR, obsCircleC}, alpha1, alpha2, totalLength, 1, 1);

        return res;
    }

    // calculate lsl type path.
    public TrajectoryResult calculateLSL(int robotR, int robotC,
                                         int targetR, int targetC,
                                         int robotCircleR, int robotCircleC,
                                         int obsCircleR, int obsCircleC){

        int l = calculateEuclideanDistance(obsCircleR, obsCircleC, robotCircleR, robotCircleC);
        // calculate v1 which points from center p1 to center p2
        double v1R = obsCircleR - robotCircleR;
        double v1C = obsCircleC - robotCircleC;
        // v2 is rotated by pi/2
        double v2R = -v1C;
        double v2C = v1R;

        // get the intermidiate pt1 position
        int pt1R = robotCircleR + (int)(v2R * r/l);
        int pt1C = robotCircleC + (int)(v2C * r/l);

        // get the intermidiate pt2 position
        int pt2R = pt1R + (int)v1R;
        int pt2C = pt1C + (int)v1C;

        // calculate the 2 arcs
        int p1pR = robotR - robotCircleR;
        int p1pC = robotC - robotCircleC;
        int p1pt1R = pt1R - robotCircleR;
        int p1pt1C = pt1C - robotCircleC;

        int alpha1 = (int)calculateArcAngle(p1pR, p1pC, p1pt1R, p1pt1C, 0);
        int arc1 = calculateArcLength(p1pR, p1pC, p1pt1R, p1pt1C, 0);

        int p2pt2R = pt2R - obsCircleR;
        int p2pt2C = pt2C - obsCircleC;
        int p2pR = targetR - obsCircleR;
        int p2pC = targetC - obsCircleC;

        int alpha2 = (int)calculateArcAngle(p2pt2R, p2pt2C, p2pR, p2pC, 0);
        int arc2 = calculateArcLength(p2pt2R, p2pt2C, p2pR, p2pC, 0);

        int totalLength = arc1 + arc2 + l;

        TrajectoryResult res = new TrajectoryResult(new int[]{pt1R, pt1C}, new int[]{pt2R, pt2C}, new int[]{robotCircleR,
                robotCircleC}, new int[]{obsCircleR, obsCircleC}, alpha1, alpha2, totalLength, 0, 0);

        return res;
    }


    // calculate lsr type path.
    public TrajectoryResult calculateLSR(int robotR, int robotC,
                                         int targetR, int targetC,
                                         int robotCircleR, int robotCircleC,
                                         int obsCircleR, int obsCircleC) {

        int d = calculateEuclideanDistance(robotCircleR, robotCircleC, obsCircleR, obsCircleC);
        int l = (int)Math.sqrt(Math.pow(d, 2) - 4*Math.pow(r, 2));

        int theta = (int)Math.acos(2*r/d); // return a value from 0 to pi

        // the vector from p1 to p2
        double v1R = obsCircleR - robotCircleR;
        double v1C = obsCircleC - robotCircleC;

        // rotation of v1 by angle theta
        // if rsl check if turning right
        double v2R;
        double v2C;
        v2R = (v1R * Math.cos(theta) + v1C * Math.sin(theta));
        v2C = (- v1R * Math.sin(theta) + v1C * Math.cos(theta));

        // point pt1
        double pt1R = obsCircleR + (r/d) * v2R;
        double pt1C = obsCircleC + (r/d) * v2C;


        // reversing the direction of v2 to get v3.
        double v3R = -v2R;
        double v3C = -v2C;

        // point pt2
        double pt2R = obsCircleR + r/d * v3R;
        double pt2C = obsCircleC + r/d * v3C;

        // calculate the 2 arcs
        int p1pR = robotR - robotCircleR;
        int p1pC = robotC - robotCircleC;
        int p1pt1R = (int)pt1R - robotCircleR;
        int p1pt1C = (int)pt1C - robotCircleC;

        int alpha1 = (int)calculateArcAngle(p1pR, p1pC, p1pt1R, p1pt1C, 0);
        int arc1 = calculateArcLength(p1pR, p1pC, p1pt1R, p1pt1C, 0);

        int p2pt2R = (int)pt2R - obsCircleR;
        int p2pt2C = (int)pt2C - obsCircleC;
        int p2pR = targetR - obsCircleR;
        int p2pC = targetC - obsCircleC;

        int alpha2 = (int)calculateArcAngle(p2pt2R, p2pt2C, p2pR, p2pC, 1);
        int arc2 = calculateArcLength(p2pt2R, p2pt2C, p2pR, p2pC, 1);

        int totalLength = arc1 + arc2 + l;

        TrajectoryResult res = new TrajectoryResult(new int[]{(int)pt1R, (int)pt1C}, new int[]{(int)pt2R, (int)pt2C}, new int[]{robotCircleR, robotCircleC},
                new int[]{obsCircleR, obsCircleC}, alpha1, alpha2, totalLength, 0, 1);

        return res;

    }

    // calculate rsl type path.
    public TrajectoryResult calculateRSL(int robotR, int robotC,
                                         int targetR, int targetC,
                                         int robotCircleR, int robotCircleC,
                                         int obsCircleR, int obsCircleC) {

        int d = calculateEuclideanDistance(robotCircleR, robotCircleC, obsCircleR, obsCircleC);
        int l = (int)Math.sqrt(Math.pow(d, 2) - 4*Math.pow(r, 2));

        int theta = (int)Math.acos(2*r/d); // return a value from 0 to pi

        // the vector from p1 to p2
        double v1R = obsCircleR - robotCircleR;
        double v1C = obsCircleC - robotCircleC;

        // rotation of v1 by angle theta
        // if rsl check if turning right
        double v2R;
        double v2C;
        v2R = (v1R * Math.cos(theta) - v1C * Math.sin(theta));
        v2C = (v1R * Math.sin(theta) + v1C * Math.cos(theta));

        // point pt1
        double pt1R = obsCircleR + (r/d) * v2R;
        double pt1C = obsCircleC + (r/d) * v2C;


        // reversing the direction of v2 to get v3.
        double v3R = -v2R;
        double v3C = -v2C;

        // point pt2
        double pt2R = obsCircleR + r/d * v3R;
        double pt2C = obsCircleC + r/d * v3C;

        // calculate the 2 arcs
        int p1pR = robotR - robotCircleR;
        int p1pC = robotC - robotCircleC;
        int p1pt1R = (int)pt1R - robotCircleR;
        int p1pt1C = (int)pt1C - robotCircleC;

        int alpha1 = (int)calculateArcAngle(p1pR, p1pC, p1pt1R, p1pt1C, 1);
        int arc1 = calculateArcLength(p1pR, p1pC, p1pt1R, p1pt1C, 1);

        int p2pt2R = (int)pt2R - obsCircleR;
        int p2pt2C = (int)pt2C - obsCircleC;
        int p2pR = targetR - obsCircleR;
        int p2pC = targetC - obsCircleC;

        int alpha2 = (int)calculateArcAngle(p2pt2R, p2pt2C, p2pR, p2pC, 0);
        int arc2 = calculateArcLength(p2pt2R, p2pt2C, p2pR, p2pC, 0);

        int totalLength = arc1 + arc2 + l;

        TrajectoryResult res = new TrajectoryResult(new int[]{(int)pt1R, (int)pt1C}, new int[]{(int)pt2R, (int)pt2C},
                new int[]{robotCircleR, robotCircleC}, new int[]{obsCircleR, obsCircleC}, alpha1, alpha2, totalLength, 1, 0);

        return res;

    }

}
