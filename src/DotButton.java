import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class DotButton extends JButton {

	private static final long serialVersionUID = 1L;

	private static final int NUM_COLOURS = 3;

	private int type;

	private int row, column;

	private static final ImageIcon[] icons = new ImageIcon[NUM_COLOURS];

	public DotButton(int row, int column, int type) {
		this.row = row;
		this.column = column;
		this.type = type;
		setBackground(Color.WHITE);
		setIcon(getImageIcon());
		Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		setBorder(emptyBorder);
		setBorderPainted(false);
	}

	private ImageIcon getImageIcon() {

		if (icons[type] == null) {
			String strId = Integer.toString(type);
			icons[type] = new ImageIcon("data/ball-" + strId + ".png");
		}
		return icons[type];
	}

	public void setType(int type) {
		this.type = type;
		setIcon(getImageIcon());
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
}
