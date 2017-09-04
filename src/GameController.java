import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.io.*;

import javax.swing.*;

public class GameController implements ActionListener {

	private GameView gameView;

	private GameModel gameModel, newUndoModel, newRedoModel;
	private LinkedStack<GameModel> undoGameStack;
	private LinkedStack<GameModel> redoGameStack;

	public GameController(int size) {
		gameModel = new GameModel(size);
		gameView = new GameView(gameModel, this);
		undoGameStack = new LinkedStack<GameModel>();
		redoGameStack = new LinkedStack<GameModel>();
		gameView.update();
	}

	public void reset() {

		gameModel.reset();
		gameView.update();

		undoGameStack = new LinkedStack<GameModel>();
		redoGameStack = new LinkedStack<GameModel>();
		gameView.enableUndo(!undoGameStack.isEmpty());
		gameView.enableRedo(!redoGameStack.isEmpty());
		System.out.println("undoGameStack: " + undoGameStack.isEmpty() + " redoGameStack: " + redoGameStack.isEmpty());
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof DotButton) {
			DotButton clicked = (DotButton) (e.getSource());

			if (gameModel.getCurrentStatus(clicked.getColumn(), clicked.getRow()) == GameModel.AVAILABLE) {

				// Emoty the redoGameStack
				redoGameStack = new LinkedStack<GameModel>();

				// Push current state of game to UndoGameStack
				try {
					newUndoModel = (GameModel) gameModel.clone();
					undoGameStack.push(newUndoModel);
				} catch (CloneNotSupportedException f) {
					System.out.println("Cloning into undoGameStack failed");
				}
				gameView.enableUndo(!undoGameStack.isEmpty());
				gameView.enableRedo(!redoGameStack.isEmpty());
				gameModel.select(clicked.getColumn(), clicked.getRow());
				oneStep();
			}

		} else if (e.getSource() instanceof JButton) {
			JButton clicked = (JButton) (e.getSource());

			if (clicked.getText().equals("Quit")) {
				try {
					gameSave(gameModel, "savedGame.txt");
				} catch (IOException g) {
					System.out.println("something happened I dont know");
				}
				System.exit(0);

			} else if (clicked.getText().equals("Undo")) {
				if (!undoGameStack.isEmpty()) {

					// add stack to redo
					try {
						GameModel newRedoModel = (GameModel) gameModel.clone();
						redoGameStack.push(newRedoModel);

						System.out.println("Added stack to redoGameStack");
					} catch (CloneNotSupportedException f) {
						System.out.println("Cloning into redoGameStack failed");
					}

					// undo function
					GameModel undoGameModel = undoGameStack.pop();
					gameModel.changeModel(undoGameModel.getModel(), undoGameModel.getCurrentDot());
					gameView.enableUndo(!undoGameStack.isEmpty());
					gameView.enableRedo(!redoGameStack.isEmpty());
					gameView.update();

				} else {
					System.out.println("Stack is empty!");
				}
				gameView.update();

			} else if (clicked.getText().equals("Reset")) {
				this.reset();
			} else if (clicked.getText().equals("Redo")) {
				if (!redoGameStack.isEmpty()) {

					// add stack to undoGameStack;

					try {
						newUndoModel = (GameModel) gameModel.clone();
						undoGameStack.push(newUndoModel);
					} catch (CloneNotSupportedException g) {
						System.out.println("Cloning into redoGameStack failed");
					}

					// redo function
					GameModel redoGameModel = redoGameStack.pop();
					gameModel.changeModel(redoGameModel.getModel(), redoGameModel.getCurrentDot());
					gameView.enableUndo(!undoGameStack.isEmpty());
					gameView.enableRedo(!redoGameStack.isEmpty());
					gameView.update();
				}
			}
		}
	}

	private void oneStep() {
		Point currentDot = gameModel.getCurrentDot();
		if (isOnBorder(currentDot)) {
			gameModel.setCurrentDot(-1, -1);
			gameView.update();

			Object[] options = { "Play Again", "Quit" };
			int n = JOptionPane.showOptionDialog(gameView, "You lost! Would you like to play again?", "Lost",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (n == 0) {
				reset();
			} else {
				System.exit(0);
			}
		} else {
			Point direction = findDirection();
			if (direction.getX() == -1) {
				gameView.update();

				Object[] options = { "Play Again", "Quit" };
				int n = JOptionPane.showOptionDialog(gameView,
						"Congratualtions, you won in " + gameModel.getNumberOfSteps()
								+ " steps!\n Would you like to play again?",
						"Won", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 0) {
					reset();
				} else {
					System.exit(0);
				}
			} else {
				gameModel.setCurrentDot(direction.getX(), direction.getY());
				gameView.update();
			}
		}

	}

	private Point findDirection() {
		boolean[][] blocked = new boolean[gameModel.getSize()][gameModel.getSize()];

		for (int i = 0; i < gameModel.getSize(); i++) {
			for (int j = 0; j < gameModel.getSize(); j++) {
				blocked[i][j] = !(gameModel.getCurrentStatus(i, j) == GameModel.AVAILABLE);
			}
		}

		Queue<Pair<Point>> myQueue = new LinkedQueue<Pair<Point>>();

		LinkedList<Point> possibleNeighbours = new LinkedList<Point>();

		// start with neighbours of the current dot
		// (note: we know the current dot isn't on the border)
		Point currentDot = gameModel.getCurrentDot();

		possibleNeighbours = findPossibleNeighbours(currentDot, blocked);

		// adding some non determinism into the game !
		java.util.Collections.shuffle(possibleNeighbours);

		for (int i = 0; i < possibleNeighbours.size(); i++) {
			Point p = possibleNeighbours.get(i);
			if (isOnBorder(p)) {
				return p;
			}
			myQueue.enqueue(new Pair<Point>(p, p));
			blocked[p.getX()][p.getY()] = true;
		}

		// start the search
		while (!myQueue.isEmpty()) {
			Pair<Point> pointPair = myQueue.dequeue();
			possibleNeighbours = findPossibleNeighbours(pointPair.getFirst(), blocked);

			for (int i = 0; i < possibleNeighbours.size(); i++) {
				Point p = possibleNeighbours.get(i);
				if (isOnBorder(p)) {
					return pointPair.getSecond();
				}
				myQueue.enqueue(new Pair<Point>(p, pointPair.getSecond()));
				blocked[p.getX()][p.getY()] = true;
			}

		}

		// could not find a way out. Return an outside direction
		return new Point(-1, -1);

	}

	private boolean isOnBorder(Point p) {
		return (p.getX() == 0 || p.getX() == gameModel.getSize() - 1 || p.getY() == 0
				|| p.getY() == gameModel.getSize() - 1);
	}

	private LinkedList<Point> findPossibleNeighbours(Point point, boolean[][] blocked) {

		LinkedList<Point> list = new LinkedList<Point>();
		int delta = (point.getY() % 2 == 0) ? 1 : 0;
		if (!blocked[point.getX() - delta][point.getY() - 1]) {
			list.add(new Point(point.getX() - delta, point.getY() - 1));
		}
		if (!blocked[point.getX() - delta + 1][point.getY() - 1]) {
			list.add(new Point(point.getX() - delta + 1, point.getY() - 1));
		}
		if (!blocked[point.getX() - 1][point.getY()]) {
			list.add(new Point(point.getX() - 1, point.getY()));
		}
		if (!blocked[point.getX() + 1][point.getY()]) {
			list.add(new Point(point.getX() + 1, point.getY()));
		}
		if (!blocked[point.getX() - delta][point.getY() + 1]) {
			list.add(new Point(point.getX() - delta, point.getY() + 1));
		}
		if (!blocked[point.getX() - delta + 1][point.getY() + 1]) {
			list.add(new Point(point.getX() - delta + 1, point.getY() + 1));
		}
		return list;
	}

	private void gameSave(GameModel givenModel, String dst) throws IOException, FileNotFoundException {

		OutputStreamWriter output;

		output = new OutputStreamWriter(new FileOutputStream(dst));

		output.write(gameModel.getSize() + "\n");
		String textSave;
		for (int a = 0; a < givenModel.getSize(); a++) {
			textSave = "";
			for (int b = 0; b < givenModel.getSize(); b++) {
				textSave = textSave + givenModel.getModel()[a][b] + "\n";
			}
			output.write(textSave);
		}
		output.close();
	}
}