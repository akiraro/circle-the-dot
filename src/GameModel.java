import java.util.Random;
import java.io.*;

public class GameModel implements Cloneable {

	public static final int AVAILABLE = 0;
	public static final int SELECTED = 1;
	public static final int DOT = 2;

	private static final int INITIAL_PROBA = 10;

	private int sizeOfGame;

	private int[][] model;

	private Point currentDot;

	private int numberOfSteps;

	private Random generator;

	public GameModel(int size) {
		numberOfSteps = 0;
		generator = new Random();
		sizeOfGame = size;
		reset();
	}

	public void reset() {

		model = new int[sizeOfGame][sizeOfGame];
		for (int i = 0; i < sizeOfGame; i++) {
			for (int j = 0; j < sizeOfGame; j++) {
				model[i][j] = AVAILABLE;
			}
		}

		// on a odd board, put the current dot randomly on a centered square of
		// 2 by 2, on an even board, put the current dot randomly on a centered
		// square of 3 by 3

		if (sizeOfGame % 2 == 0) {
			currentDot = new Point(sizeOfGame / 2 - generator.nextInt(2), sizeOfGame / 2 - generator.nextInt(2));
		} else {
			currentDot = new Point(sizeOfGame / 2 + 1 - generator.nextInt(3),
					sizeOfGame / 2 + 1 - generator.nextInt(3));
		}

		model[currentDot.getX()][currentDot.getY()] = DOT;

		for (int i = 0; i < sizeOfGame; i++) {
			for (int j = 0; j < sizeOfGame; j++) {
				if (!(i == currentDot.getX() && j == currentDot.getY())) {
					if (generator.nextInt(INITIAL_PROBA) == 0) {
						model[i][j] = SELECTED;
					}
				}
			}
		}

		numberOfSteps = 0;
	}

	public int getSize() {
		return sizeOfGame;
	}

	public int[][] getModel() {
		return model;
	}

	public int getCurrentStatus(int i, int j) {
		return model[i][j];
	}

	public void select(int i, int j) {
		model[i][j] = SELECTED;
		numberOfSteps++;
	}

	public void setCurrentDot(int i, int j) {
		model[currentDot.getX()][currentDot.getY()] = AVAILABLE;
		// pass on "-1" to remove the current dot at the end of the game
		if (i != -1) {
			model[i][j] = DOT;
			currentDot.reset(i, j);
		}
	}

	public Point getCurrentDot() {
		return currentDot;
	}

	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	public void changeModel(int[][] givenModel, Point givenPoint) {
		this.setCurrentDot(givenPoint.getX(), givenPoint.getY());
		for (int a = 0; a < sizeOfGame; a++) {
			for (int b = 0; b < sizeOfGame; b++) {
				model[b][a] = givenModel[b][a];
			}
		}
	}

	public void changeModel(int[][] givenModel) {
		for (int a = 0; a < sizeOfGame; a++) {
			for (int b = 0; b < sizeOfGame; b++) {
				model[b][a] = givenModel[b][a];
			}
		}
	}

	public Object clone() throws CloneNotSupportedException {
		GameModel newModel = new GameModel(this.sizeOfGame);

		for (int a = 0; a < sizeOfGame; a++) {
			for (int b = 0; b < sizeOfGame; b++) {
				newModel.model[b][a] = this.model[b][a];
			}
		}
		newModel.setCurrentDot(this.getCurrentDot().getX(), this.getCurrentDot().getY());
		return newModel;
	}
}
