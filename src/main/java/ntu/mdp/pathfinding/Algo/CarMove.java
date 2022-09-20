package ntu.mdp.pathfinding.Algo;

import java.util.ArrayList;

public class CarMove {
    private int turnTheta1, turnTheta2, turnTheta3;
    private boolean isClockwise1, isClockwise2, isClockwise3;
    private int moveLength;
    private ArrayList<String> instructionList;

    // car move type of Curve-StraightLine-Curve.
    public CarMove(int turnTheta1, boolean isClockwise1,
                   int turnTheta2, boolean isClockwise2,
                   int moveLength) {
        this.turnTheta1 = turnTheta1;
        this.isClockwise1 = isClockwise1;
        this.turnTheta2 = turnTheta2;
        this.isClockwise2 = isClockwise2;
        this.moveLength = moveLength;
        this.instructionList = new ArrayList<>();

        // adding instructions into the arraylist.

        if (isClockwise1) // first curve turning right
            instructionList.add(CarMoveInstructions.turnRightPrefix + turnTheta1);
        else instructionList.add(CarMoveInstructions.turnLeftPrefix + turnTheta1);

        if (moveLength >= 0)
            instructionList.add(CarMoveInstructions.moveForwardPrefix + conversionToInstructionFormat(moveLength)); // goingStraight
        else instructionList.add(CarMoveInstructions.moveBackwardPrefix + conversionToInstructionFormat(Math.abs(moveLength)));

        if (isClockwise2)
            instructionList.add(CarMoveInstructions.turnRightPrefix + turnTheta2);
        else instructionList.add(CarMoveInstructions.turnLeftPrefix + turnTheta2);
    }

    // car move type of Curve-Curve-CurveType.
    public CarMove(int turnTheta1, boolean isClockwise1,
                   int turnTheta2, boolean isClockwise2,
                   int turnTheta3, boolean isClockwise3){
        this.turnTheta1 = turnTheta1;
        this.isClockwise1 = isClockwise1;
        this.turnTheta2 = turnTheta2;
        this.isClockwise2 = isClockwise2;
        this.turnTheta3 = turnTheta3;
        this.isClockwise3 = isClockwise3;
        this.instructionList = new ArrayList<>();

        // adding instructions into the arraylist

        if (isClockwise1)
            instructionList.add(CarMoveInstructions.turnRightPrefix + turnTheta1);
        else instructionList.add(CarMoveInstructions.turnLeftPrefix + turnTheta1);

        if (isClockwise2)
            instructionList.add(CarMoveInstructions.turnRightPrefix + turnTheta2);
        else instructionList.add(CarMoveInstructions.turnLeftPrefix + turnTheta2);

        if (isClockwise3)
            instructionList.add(CarMoveInstructions.turnRightPrefix + turnTheta3);
        else instructionList.add(CarMoveInstructions.turnLeftPrefix + turnTheta3);
    }

    // car move type of straightline only
    public CarMove(int moveLength){
        this.instructionList = new ArrayList<>();

        if (moveLength >= 0)
            instructionList.add(CarMoveInstructions.moveForwardPrefix + conversionToInstructionFormat(moveLength)); // goingStraight
        else instructionList.add(CarMoveInstructions.moveBackwardPrefix + conversionToInstructionFormat(Math.abs(moveLength)));


    }

    private String conversionToInstructionFormat(int moveLength){
        int moveLenInCM = moveLength * 5; // one grid is 5 cm

        if (moveLenInCM / 100 == 0) // 2 digit number
            return "0" + moveLenInCM;
        else
            return Integer.toString(moveLenInCM);
    }


    @Override
    public String toString() {
        return "CarMove{" +
                "turnTheta1=" + turnTheta1 +
                ", turnTheta2=" + turnTheta2 +
                ", moveLength=" + moveLength +
                '}';
    }

    public ArrayList<String> getInstructions(){
        return this.instructionList;
    }
}
