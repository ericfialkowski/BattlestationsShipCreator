package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.PDFShipWriterOptions;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_END;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.Box.createHorizontalGlue;
import static javax.swing.Box.createRigidArea;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.BoxLayout.PAGE_AXIS;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;
import javax.swing.UnsupportedLookAndFeelException;

public class PDFPreferencesDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private static final String OKACTION = "OKACTION";
	private static final String CANCELACTION = "CANCELACTION";
	private static final String DEFAULTSACTION = "DEFAULTSACTION";

	private JCheckBox showNotes;
	private JCheckBox showSpeed;
	private JCheckBox showOOC;
	private JCheckBox showHelmPower;
	private JCheckBox showGunPower;
	private JCheckBox showShieldPower;
	private JCheckBox reverseShieldPower;

	private JButton cancelButton;
	private JButton okButton;
	private JButton defaultsButton;

	private JRadioButton letterRadio;
	private JRadioButton legalRadio;
	private ButtonGroup paperSizeGroup;

	private JRadioButton damageChartRadio;
	private JRadioButton damageTrackRadio;
	private JRadioButton damageNoneRadio;
	private ButtonGroup damageGroup;

	private JSlider qualitySlider;

	public PDFPreferencesDialog()
	{
		super();
		initUI();
		setLocationRelativeTo(null);
	}

	public PDFPreferencesDialog(JFrame frame)
	{
		super(frame);
		initUI();
		setLocationRelativeTo(frame);
	}

	private void initUI()
	{
		setTitle("PDF Preferences");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		ImageIcon icon = new ImageIcon(getClass().getResource("/img/icon.gif"));
		setIconImage(icon.getImage());

		JPanel holder = new JPanel();
		holder.setLayout(new BoxLayout(holder, PAGE_AXIS));
		holder.setBorder(createEmptyBorder(10, 10, 10, 10));

		showNotes = new JCheckBox("Show Notes");
		showSpeed = new JCheckBox("Show Speed Track");
		showOOC = new JCheckBox("Show OOC Track");
		showHelmPower = new JCheckBox("Show Helm Power Track");
		showGunPower = new JCheckBox("Show Gun Power Track");
		showShieldPower = new JCheckBox("Show Shield Power Track");
		reverseShieldPower = new JCheckBox("Reverse Shield Power Track");

		JPanel controlChartPanel = new JPanel();
		controlChartPanel.setLayout(new GridLayout(7, 1, 2, 2));
		controlChartPanel.setBorder(createTitledBorder("Control Chart Options"));
		controlChartPanel.add(showNotes);
		controlChartPanel.add(showSpeed);
		controlChartPanel.add(showOOC);
		controlChartPanel.add(showHelmPower);
		controlChartPanel.add(showGunPower);
		controlChartPanel.add(showShieldPower);
		controlChartPanel.add(reverseShieldPower);

		holder.add(controlChartPanel);

		damageChartRadio = new JRadioButton("Damage Chart");
		damageTrackRadio = new JRadioButton("Damage Track");
		damageNoneRadio = new JRadioButton("None");
		damageNoneRadio.setSelected(true);
		damageGroup = new ButtonGroup();
		damageGroup.add(damageChartRadio);
		damageGroup.add(damageTrackRadio);
		damageGroup.add(damageNoneRadio);
		JPanel damagePanel = new JPanel();
		damagePanel.setLayout(new BoxLayout(damagePanel, LINE_AXIS));
		damagePanel.setBorder(createTitledBorder("Damage Chart Options"));
		damagePanel.add(damageChartRadio);
		damagePanel.add(createRigidArea(new Dimension(10, 0)));
		damagePanel.add(damageTrackRadio);
		damagePanel.add(createRigidArea(new Dimension(10, 0)));
		damagePanel.add(damageNoneRadio);
		damagePanel.add(createHorizontalGlue());

		holder.add(damagePanel);

		qualitySlider = new JSlider(HORIZONTAL, 0, 75, 0);
		qualitySlider.setMinorTickSpacing(5);
		qualitySlider.setMajorTickSpacing(25);
		JPanel qualityPanel = new JPanel();
		qualityPanel.setLayout(new BoxLayout(qualityPanel, LINE_AXIS));
		qualityPanel.setBorder(createTitledBorder("Ship Image Quality"));
		qualityPanel.add(new JLabel("Highest"));
		damagePanel.add(createRigidArea(new Dimension(3, 0)));
		qualityPanel.add(qualitySlider);
		damagePanel.add(createRigidArea(new Dimension(3, 0)));
		qualityPanel.add(new JLabel("Lowest"));
		qualityPanel.add(createHorizontalGlue());

		holder.add(qualityPanel);

		letterRadio = new JRadioButton("Letter");
		legalRadio = new JRadioButton("Legal");
		paperSizeGroup = new ButtonGroup();
		paperSizeGroup.add(letterRadio);
		paperSizeGroup.add(legalRadio);
		JPanel pageSizePanel = new JPanel();
		pageSizePanel.setLayout(new BoxLayout(pageSizePanel, LINE_AXIS));
		pageSizePanel.setBorder(createTitledBorder("Paper Size"));
		pageSizePanel.add(letterRadio);
		pageSizePanel.add(createRigidArea(new Dimension(10, 0)));
		pageSizePanel.add(legalRadio);
		pageSizePanel.add(createHorizontalGlue());

		holder.add(pageSizePanel);

		defaultsButton = new JButton("Defaults");
		defaultsButton.setActionCommand(DEFAULTSACTION);
		defaultsButton.addActionListener(this);

		okButton = new JButton("OK");
		okButton.setActionCommand(OKACTION);
		okButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCELACTION);
		cancelButton.addActionListener(this);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, LINE_AXIS));
		buttonPane.setBorder(createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(defaultsButton);
		buttonPane.add(createRigidArea(new Dimension(5, 0)));
		buttonPane.add(createHorizontalGlue());
		buttonPane.add(okButton);
		buttonPane.add(createRigidArea(new Dimension(10, 0)));
		buttonPane.add(cancelButton);

		add(holder, CENTER);
		add(buttonPane, PAGE_END);

		pack();
		setResizable(false);
		correlatePrefs(true);
	}

	private void correlatePrefs(boolean load)
	{
		PDFShipWriterOptions options = new PDFShipWriterOptions();
		if (load)
		{
			options.loadPreferences();
		}
		correlatePrefs(options);
	}

	private void correlateFleetPrefs()
	{
		PDFShipWriterOptions options = new PDFShipWriterOptions();
		options.setShowHelm(false);
		options.setShowGuns(true);
		options.setShowOCC(false);
		options.setShowSpeed(true);
		options.setShowShield(true);
		options.setReverseShield(true);
		options.setDamageChart(true);

		correlatePrefs(options);
	}

	private void correlatePrefs(PDFShipWriterOptions options)
	{

		showNotes.setSelected(options.isShowNotes());
		showSpeed.setSelected(options.isShowSpeed());
		showOOC.setSelected(options.isShowOCC());
		showHelmPower.setSelected(options.isShowHelm());
		showGunPower.setSelected(options.isShowGuns());
		showShieldPower.setSelected(options.isShowShield());
		reverseShieldPower.setSelected(options.isReverseShield());

		switch (options.getPageSize())
		{
			case 1:
				legalRadio.setSelected(true);
				break;
			default:
				letterRadio.setSelected(true);
		}

		damageChartRadio.setSelected(options.isDamageChart());
		damageTrackRadio.setSelected(options.isDamageTrack());

		float quality = options.getOutputQualityReduction();
		qualitySlider.setValue((int) (quality * 100));
	}

	private void setPrefs()
	{
		PDFShipWriterOptions options = new PDFShipWriterOptions();

		options.setShowNotes(showNotes.isSelected());
		options.setShowHelm(showHelmPower.isSelected());
		options.setShowGuns(showGunPower.isSelected());
		options.setShowOCC(showOOC.isSelected());
		options.setShowSpeed(showSpeed.isSelected());
		options.setShowShield(showShieldPower.isSelected());
		options.setReverseShield(reverseShieldPower.isSelected());

		if (legalRadio.isSelected())
		{
			options.setPageSize(1);
		}
		else
		{
			options.setPageSize(0);
		}

		options.setDamageChart(damageChartRadio.isSelected());
		options.setDamageTrack(damageTrackRadio.isSelected());

		float quality = qualitySlider.getValue() / 100.0F;
		options.setOutputQualityReduction(quality);

		options.savePreferences();
	}

	public static void main(String[] args)
	{
		invokeLater(()
			->
			{
				JFrame.setDefaultLookAndFeelDecorated(true);
				try
				{
					// Set System L&F
					setLookAndFeel(getSystemLookAndFeelClassName());
				}
				catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
				{
					// handle exception
				}
				PDFPreferencesDialog dialog = new PDFPreferencesDialog();
				dialog.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		switch (actionCommand)
		{
			case OKACTION:
				setPrefs();
				setVisible(false);
				dispose();
				break;
			case CANCELACTION:
				setVisible(false);
				dispose();
				break;
			default:
				correlatePrefs(false);
		}

	}

}
