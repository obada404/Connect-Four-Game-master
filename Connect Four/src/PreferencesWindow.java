import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class PreferencesWindow extends JFrame {

	private JLabel modeLabel;
	private JLabel diffLabel;
	private JLabel P1Label;
	private JLabel P2Label;

	public JComboBox<String> gameMode;
	public JComboBox<String> difficulty;
	public JTextField P1Name;
	public JTextField P2Name;

	private JButton apply;
	private JButton cancel;

	private EventHandler handler;
	private GameParameters game_params;

	public PreferencesWindow(GameParameters gp) {
		super("Preferences");
		this.setAlwaysOnTop(true);
		this.game_params = gp;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setSize(403, 263);
		setLocationRelativeTo(Main.panelMain);
		setResizable(false);

		handler = new EventHandler();

		modeLabel = new JLabel("Game Mode: ");
		diffLabel = new JLabel("Difficulty:");
		P1Label = new JLabel("Player Name:");
		P2Label = new JLabel("Player2 Name:");
		getContentPane().add(modeLabel);
		getContentPane().add(diffLabel);
		getContentPane().add(P1Label);
		getContentPane().add(P2Label);
		modeLabel.setBounds(20, 25, 175, 30);
		diffLabel.setBounds(20, 175, 175, 30);
		P1Label.setBounds(20, 75, 175, 30);
		P2Label.setBounds(20, 125, 175, 30);

		P1Name = new JTextField();
		P1Name.setFont(new Font("Tahoma", Font.PLAIN, 13));

		P2Name = new JTextField();
		P2Name.setFont(new Font("Tahoma", Font.PLAIN, 13));

		difficulty = new JComboBox<String>();
		difficulty.setFont(new Font("Tahoma", Font.PLAIN, 13));
		difficulty.addItem("Easy");
		difficulty.addItem("Medium");
		difficulty.addItem("Hard");
		difficulty.setSelectedIndex(0);
		difficulty.setBounds(220, 175, 160, 30);

		gameMode = new JComboBox<String>();
		gameMode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (((String) gameMode.getSelectedItem()).equals("Human VS Computer")) {
					P2Label.setVisible(false);
					P2Name.setVisible(false);
					P1Label.setText("Player Name");

					PreferencesWindow.this.difficulty.setVisible(true);
					PreferencesWindow.this.difficulty.setLocation(220, 125);

					diffLabel.setVisible(true);
					diffLabel.setLocation(20, 125);
				} else if (((String) gameMode.getSelectedItem()).equals("Human VS Human")) {
					PreferencesWindow.this.difficulty.setVisible(false);

					diffLabel.setVisible(false);

					P2Name.setVisible(true);
					P2Label.setVisible(true);
					P1Label.setText("Player1 Name");
				}
			}
		});

		gameMode.setFont(new Font("Tahoma", Font.PLAIN, 13));
		gameMode.addItem("Human VS Computer");
		gameMode.addItem("Human VS Human");
		gameMode.addActionListener(handler);

		int selectedMode = game_params.getGameMode();
		if (selectedMode == GameParameters.HumanVSComputer)
			gameMode.setSelectedIndex(0);
		else if (selectedMode == GameParameters.HumanVSHuman)
			gameMode.setSelectedIndex(1);

		getContentPane().add(gameMode);
		getContentPane().add(difficulty);
		getContentPane().add(P1Name);
		getContentPane().add(P2Name);
		gameMode.setBounds(220, 25, 160, 30);

		P1Name.setBounds(220, 75, 160, 30);
		P2Name.setBounds(220, 125, 160, 30);

		apply = new JButton("Apply");
		apply.setFont(new Font("Tahoma", Font.PLAIN, 12));
		cancel = new JButton("Cancel");
		cancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		getContentPane().add(apply);
		getContentPane().add(cancel);
		apply.setBounds(220, 190, 100, 30);
		apply.addActionListener(handler);
		cancel.setBounds(80, 190, 100, 30);
		cancel.addActionListener(handler);
	}

	private class EventHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			if (ev.getSource() == cancel) {
				dispose();
			} else if (ev.getSource() == apply) {
				try {
					if ((gameMode.getSelectedIndex() == 1 && P2Name.getText().isEmpty())
							|| P1Name.getText().isEmpty()) {
						PreferencesWindow.this.setAlwaysOnTop(false);
						JOptionPane.showMessageDialog(PreferencesWindow.this, "Please fill out all the fields!",
								"Data Missing", JOptionPane.WARNING_MESSAGE);
						PreferencesWindow.this.setAlwaysOnTop(true);
					} else {
						String game_mode = (String) gameMode.getSelectedItem();
						String diff = (String) difficulty.getSelectedItem();
						String player1_Name = P1Name.getText();
						String player2_Name = P2Name.getText();

						int gameModeCode = (game_mode.equals("Human VS Computer")) ? GameParameters.HumanVSComputer
								: GameParameters.HumanVSHuman;

						game_params.setGameMode(gameModeCode);
						game_params.setDifficulty(diff);
						game_params.setPlayer1Name(player1_Name);
						game_params.setPlayer2Name(player2_Name);

						Main.player1ScoreValue = 0;
						Main.player2ScoreValue = 0;
						Main.createNewGame();
						dispose();
					}
				} catch (Exception e) {
					System.err.println("ERROR : " + e.getMessage());
				}
			}
		}
	}
}
