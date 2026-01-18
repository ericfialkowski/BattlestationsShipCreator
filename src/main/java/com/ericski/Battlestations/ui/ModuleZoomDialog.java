package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.Module;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

public class ModuleZoomDialog extends JDialog
{
	private final ModuleSelectionPanel msp;

	public ModuleZoomDialog(Module module)
	{
		super();
		setTitle(module.getDescription());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource("/img/tentac_ship_icon.png"));
		setIconImage(icon.getImage());
		this.msp = new ModuleSelectionPanel(false);
		msp.setWidth(250);
		msp.setHeight(250);
		msp.setModule(module);
		this.add(msp);
//
//        Would be nice to do
//
//        addKeyListener(new KeyAdapter()
//        {
//            @Override
//            public void keyReleased(KeyEvent e)
//            {
//                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER
//                    || e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
//                {
//                    // we're done for now....
//                    dispose();
//                }
//            }
//        });
		pack();
	}
}
