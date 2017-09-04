import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GameView extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Reference to the view of the board
	 */
	private BoardView board;
	private GameModel gameModel;
	private JButton buttonUndo, buttonRedo;

	public GameView(GameModel model, GameController gameController) {
		super("Circle the Dot");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);

		gameModel = model;
		board = new BoardView(model, gameController);
		add(board, BorderLayout.CENTER);

		buttonUndo = new JButton("Undo");
		buttonUndo.setEnabled(false);
		buttonUndo.setFocusPainted(false);
		buttonUndo.addActionListener(gameController);

		buttonRedo = new JButton("Redo");
		buttonRedo.setEnabled(false);
		buttonRedo.setFocusPainted(false);
		buttonRedo.addActionListener(gameController);

		JButton buttonReset = new JButton("Reset");
		buttonReset.setFocusPainted(false);
		buttonReset.addActionListener(gameController);

		JButton buttonExit = new JButton("Quit");
		buttonExit.setFocusPainted(false);
		buttonExit.addActionListener(gameController);

		JPanel control = new JPanel();
		control.setBackground(Color.WHITE);
		control.add(buttonUndo);
		control.add(buttonRedo);
		control.add(buttonReset);
		control.add(buttonExit);
		add(control, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		setVisible(true);

	}

	public void update() {
		board.update();
	}

	public void enableUndo(boolean givenBoolean) {
		buttonUndo.setEnabled(givenBoolean);
	}

	public void enableRedo(boolean givenBoolean) {
		buttonRedo.setEnabled(givenBoolean);
	}
}
