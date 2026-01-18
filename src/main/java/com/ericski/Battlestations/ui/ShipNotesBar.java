package com.ericski.Battlestations.ui;

import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import static javax.swing.Box.createHorizontalStrut;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.X_AXIS;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.SwingUtilities.invokeLater;
import org.jdesktop.swingx.JXCollapsiblePane;
import static org.jdesktop.swingx.JXCollapsiblePane.Direction.DOWN;

public class ShipNotesBar extends JPanel
{
	private static final String TOGGLEACTION = "TOGGLEACTION";

	private static final long serialVersionUID = 1L;

	ShipCreatorPanel parentPanel;
	JButton hiderButton;

	JXCollapsiblePane modulePane;
	JPanel hider;

	boolean collapsed = true;

	@Override
	public Dimension getPreferredSize()
	{
		if (collapsed)
		{
			return hider.getPreferredSize();
		}
		else
		{
			Dimension dim1 = hider.getPreferredSize();
			Dimension dim2 = modulePane.getPreferredSize();

			return new Dimension(dim1.width, dim1.height + dim2.height);
		}
	}

	public ShipNotesBar(ShipCreatorPanel parentPanel)
	{
		super(new BorderLayout());
		this.parentPanel = parentPanel;
		hider = new JGradPanel(BLACK.brighter().brighter(), WHITE);
		hider.setLayout(new BoxLayout(hider, X_AXIS));
		hiderButton = new JButton("/\\");
		hiderButton.setFont(hiderButton.getFont().deriveFont(6));
		hiderButton.setMargin(new Insets(0, 0, 0, 0));
		hider.add(hiderButton);
		hider.add(createHorizontalStrut(5));
		JLabel label = new JLabel("Ship Notes");
		label.setForeground(WHITE);
		hider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent evt)
			{
				if (!evt.isPopupTrigger())
				{
					showHideShipBar();
				}
			}
		});
		hider.add(label);
		add(hider, WEST);
		hiderButton.setActionCommand(TOGGLEACTION);
		hiderButton.addActionListener((ActionEvent e)
			->
			{
				String key = e.getActionCommand();
				if (TOGGLEACTION.equals(key))
				{
					showHideShipBar();
				}
		});

		//modulePane = new JPanel(new BorderLayout());
		modulePane = new JXCollapsiblePane(DOWN);

		modulePane.setLayout(new BorderLayout());
		modulePane.setCollapsed(true);
		modulePane.add(new JLabel("FOO"));
		modulePane.addPropertyChangeListener("collapsed", (PropertyChangeEvent evt)
											 ->
											 {
												 if ((boolean) evt.getNewValue())
												 {
													 hiderButton.setText("/\\");
													 collapsed = true;
												 }
												 else// if ("expanded".equals((String)evt.getNewValue()) )
												 {
													 hiderButton.setText("\\/");
													 collapsed = false;
												 }
										 });
		modulePane.setMaximumSize(new Dimension(400, 400));
		add(modulePane, CENTER);
	}

	private void showHideShipBar()
	{
		invokeLater(()
			->
			{
				if (modulePane.isCollapsed())
				{
					collapsed = false;
					hiderButton.setText("<>");
					modulePane.setCollapsed(false);
				}
				else
				{
					hiderButton.setText("<>");
					modulePane.setCollapsed(true);
				}
		});
	}
}
