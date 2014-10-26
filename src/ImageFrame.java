/*
	A basic extension of the java.awt.Frame class
 */

import java.awt.*;
import java.io.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;

//import symantec.itools.multimedia.ImageViewer;
public class ImageFrame extends Frame
{
    Image img;  //spatial image
    ImageData ActiveImage;  // Current image displayed in frame
    ImageData BackupImage;  // Current image before most recent process was done
    boolean verbose=false;
    
    
	public ImageFrame()
	{
	    
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setLayout(null);
		setBackground(java.awt.Color.white);
		setForeground(java.awt.Color.black);
		setSize(281,285);
		setVisible(false);
		button_Hide.setLabel("Hide");
		add(button_Hide);
		button_Hide.setBackground(java.awt.Color.lightGray);
		button_Hide.setBounds(0,0,48,24);
		add(imageViewer);
		imageViewer.setBounds(36,36,195,168);
		saveFileDialog1.setMode(FileDialog.SAVE);
		saveFileDialog1.setTitle("Save As");
		saveFileDialog1.setFile("*.jpg");
		//$$ saveFileDialog1.move(0,0);
		
		setTitle("Input");
		//}}

		//{{INIT_MENUS
		menu1.setLabel("File");
		menu1.add(miSave);
		miSave.setLabel("Save As");
		miSave.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_S,false));
		menu1.add(miHide);
		miHide.setLabel("Hide");
		mainMenuBar.add(menu1);
		menu2.setLabel("Edit");
		menu2.add(undo);
		undo.setLabel("Undo");
		undo.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Z,false));
		undo.setEnabled(false); // start with edit disabled
		mainMenuBar.add(menu2);
		menu3.setLabel("Process");
		menu3.add(thresh);
		menu3.add(negative);
		menu3.add(edgeD);
		menu3.add(contrastS);
		thresh.setLabel("Threshold");
		negative.setLabel("Negative");
		edgeD.setLabel("Edge Detect");
		contrastS.setLabel("Constrast Stretch");
		mainMenuBar.add(menu3);
		//$$ mainMenuBar.move(48,0);
		setMenuBar(mainMenuBar);
		//}}

		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		button_Hide.addActionListener(lSymAction);
		miSave.addActionListener(lSymAction);
		miHide.addActionListener(lSymAction);
		undo.addActionListener(lSymAction);
		negative.addActionListener(lSymAction);
		thresh.addActionListener(lSymAction);
		edgeD.addActionListener(lSymAction);
		contrastS.addActionListener(lSymAction);
		SymItem lSymItem = new SymItem();
		//}}
	}

	public ImageFrame(String title)
	{
		this();
		setTitle(title);
	}
	
	/**
	 *Stores the spatial image in case want to toggle
	 * the display between this and wavelet image
	 */
	public void setImage(Image image)
	{  
		int height, width;
		
		try {
			ActiveImage = new ImageData(image, "JPEG", image.getHeight(this), image.getWidth(this));
			BackupImage = new ImageData(image, "JPEG", image.getHeight(this), image.getWidth(this));
		} catch (IOException e1) {
		}
		
		img = ActiveImage.createImage();
		
		height = img.getHeight(this);
		width = img.getWidth(this);
		
		System.out.println("Going to set image)");
		try{
		   ImageIcon imageIcon = new ImageIcon(img);
	       
		   imagejLabel.setIcon(imageIcon);
		   imagejLabel.setSize(width,height);
		   

	  //  imageViewer.setImage(img);
	      imageViewer.removeAll();
	      imageViewer.add(imagejLabel);
	      imageViewer.validate();
	    }catch(Exception e)
	    { }
	    try{
	        imageViewer.setSize(width,height);
	        this.setSize(width+200, height+200);
	        repaint();       
	    } catch(Exception e) {}   
	}
	
	/**
	 * Undoes the last process done to the current image
	 * Sets ActiveImage equal to BackupImage
	 * and re-draws frame
	 */
	public void undoProcess()
	{
		ActiveImage = new ImageData(BackupImage.rows,BackupImage.cols, BackupImage, 0,0);  // set the ActiveImage to the previous state
		
		// update the display
		img = ActiveImage.createImage();
		ImageIcon imageIcon = new ImageIcon(img);
		imagejLabel.setIcon(imageIcon);
		
		undo.setEnabled(false); // undo can only be done once, so disable undo button
	}
	   
	/**
	 *Stores the currently displayed image data into
	 * a file as raw GREYSCALE image data format
	 */
	public void storeGreyscaleImage(String filename)throws IOException
	{ 
	  
	    int rows, cols, pixel, alpha, red, green,blue;
	    
	    
            
	        
        //Open up file	
        FileOutputStream file_output = new FileOutputStream(filename);
        DataOutputStream DO = new DataOutputStream(file_output);
 
 
        //Write out each pixel as integers
        rows = img.getHeight(this);
        cols = img.getWidth(this);
        int pixels[] = new int[rows*cols];
        PixelGrabber pg = new PixelGrabber(img, 0,0, cols, rows, pixels, 0, rows);
        try{ pg.grabPixels();}
        catch(InterruptedException e) {
    	    System.err.println("interrupted waiting for pixels!");
    	}
	
         
        for(int r=0; r<rows; r++)
	    for(int c=0; c<cols; c++) {
            pixel = pixels[r*cols + c];
	        alpha = (pixel >> 24) & 0xff;
            red   = (pixel >> 16) & 0xff;
            green = (pixel >>  8) & 0xff;
            blue  = (pixel      ) & 0xff;
            
            if(verbose)
    	        {System.out.println("value: " + (int)((red+green+blue)/3));
    	         System.out.println(" R,G,B: " + red +"," + green +"," + blue); }
	   
 	        DO.writeByte((int)((red+green+blue)/3));	
 	        
        }	

        //flush Stream
        DO.flush();
        //close Stream
        DO.close();

    }
	
	public void storeColorImage(String filename)throws IOException
	{
		img = ActiveImage.createImage();  //  create an Image from the ActiveImage
		
		BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();  // set bg equal to bi graphics2d object so we can draw into it
		bg.drawImage(img, 0, 0, null);  // draw img into bi
		bg.dispose();  // remove the img data in bg that we don't need anymore
		
		
		File f = new File(filename);  // open up the file
		ImageIO.write(bi, "jpg", f);  // write bi to the file
	}
    
    
    /**
	 *Stores the currently displayed image data into
	 * a file as COLOR raw image data format
	 */
	/**
	public void storeColorImage(String filename)throws IOException
	{ 
	    
	    int rows, cols, pixel, alpha, red, green,blue;
	    
	   
            
	        
        //Open up file	
        FileOutputStream file_output = new FileOutputStream(filename);
        DataOutputStream DO = new DataOutputStream(file_output);
 
 
        //Write out each pixel as integers
        rows = img.getHeight(this);
        cols = img.getWidth(this);
        int pixels[] = new int[rows*cols];
        PixelGrabber pg = new PixelGrabber(img, 0,0, cols, rows, pixels, 0, cols);
        try{ pg.grabPixels();}
        catch(InterruptedException e) {
    	    System.err.println("interrupted waiting for pixels!");
    	}
	
        System.out.println("...storing as color, "+rows +" x " + cols);
        
        for(int r=0; r<rows; r++)
	    for(int c=0; c<cols; c++) {
            pixel = pixels[r*cols + c];
	        alpha = (pixel >> 24) & 0xff;
            red   = (pixel >> 16) & 0xff;
            green = (pixel >>  8) & 0xff;
            blue  = (pixel      ) & 0xff;
            if(verbose)//verbose
    	        {System.out.println("value: " + (int)((red+green+blue)/3));
    	         System.out.println(" R,G,B: " + red +"," + green +"," + blue); }
	   
 	        DO.writeByte(red);
 	        DO.writeByte(green);
 	        DO.writeByte(blue);
        }	

        //flush Stream
        DO.flush();
        //close Stream
        DO.close();

    }
	*/
    

    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			setLocation(50, 50);
		}
		super.setVisible(b);
	}

	static public void main(String args[])
	{
		(new ImageFrame()).setVisible(true);
	}
	
	public void addNotify()
	{
	    // Record the size of the window prior to calling parents addNotify.
	    Dimension d = getSize();
	    
		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets().left, insets().top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;

	//{{DECLARE_CONTROLS
	java.awt.Button button_Hide = new java.awt.Button();
	//symantec.itools.multimedia.ImageViewer imageViewer = new symantec.itools.multimedia.ImageViewer();
	javax.swing.JScrollPane imageViewer = new javax.swing.JScrollPane();
	javax.swing.JLabel imagejLabel = new JLabel();
	java.awt.FileDialog saveFileDialog1 = new java.awt.FileDialog(this);

	//}}

	//{{DECLARE_MENUS
	java.awt.MenuBar mainMenuBar = new java.awt.MenuBar();
	java.awt.Menu menu1 = new java.awt.Menu();
	java.awt.Menu menu2 = new java.awt.Menu();
	java.awt.Menu menu3 = new java.awt.Menu();
	java.awt.MenuItem miSave = new java.awt.MenuItem();
	java.awt.MenuItem miHide = new java.awt.MenuItem();
	java.awt.MenuItem undo = new java.awt.MenuItem();
	java.awt.MenuItem thresh = new java.awt.MenuItem();
	java.awt.MenuItem negative = new java.awt.MenuItem();
	java.awt.MenuItem edgeD = new java.awt.MenuItem();
	java.awt.MenuItem contrastS = new java.awt.MenuItem();
	//}}

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowOpened(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == ImageFrame.this)
				ImageFrame_WindowOpened(event);
		}

		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == ImageFrame.this)
				Frame1_WindowClosing(event);
		}
	}
	
	void Frame1_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == button_Hide)
				buttonHide_ActionPerformed(event);
			else if (object == miSave)
				miSave_ActionPerformed(event);
			else if (object == miHide)
				miHide_ActionPerformed(event);
			else if (object == undo)
				undo_ActionPerformed(event);
			else if (object == thresh)
				threshold_ActionPerformed(event);
			else if (object == negative)
				negative_ActionPerformed(event);
			else if (object == edgeD)
				edgeDetect_ActionPerformed(event);
			else if (object == contrastS)
				contrastStretch_ActionPerformed(event);
			
		}
	}

	void buttonHide_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Hide the Frame
		setVisible(false);
		//}}
	}

    /**
      *Save Image data in raw format
      */
	void miSave_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
		String Save_filename;	 
			 
		//{{CONNECTION
		// Show the SaveFileDialog
		saveFileDialog1.setVisible(true);
		//}}
		
		//GET FILENAME
		Save_filename = (saveFileDialog1.getDirectory()).concat(saveFileDialog1.getFile()); 
		System.out.println("Save info: " + Save_filename);
		
		//Store currently displayed info
		try {
    		storeColorImage(Save_filename);
        } catch (IOException e)
        {}
		
	}

	void miHide_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Hide the Frame
		setVisible(false);
		//}}
	}
	
	void undo_ActionPerformed(java.awt.event.ActionEvent event)
	{
		undoProcess();
	}
	
	void threshold_ActionPerformed(java.awt.event.ActionEvent event)
	{
		System.out.println("threshold performing!");
		BackupImage = new ImageData(ActiveImage.rows,ActiveImage.cols, ActiveImage, 0,0); // set the BackupImage
		undo.setEnabled(true); // enable the undo button
		
		// prompt for the threshold value
		int tvalue;
		do{
			String diaInput = JOptionPane.showInputDialog(
                this,
                "Please enter threshold value(0-255)\n",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE);
			try{
				tvalue = Integer.parseInt(diaInput);
			}catch(NumberFormatException e)
			{
				tvalue = -1; // will fail the while and re-prompt user
			}
		}while(!(0 <= tvalue && tvalue <= 255));
		
		// perform the threshold operation
		ActiveImage.thresholdImage(tvalue);
		
		// update the display
		img = ActiveImage.createImage();
		ImageIcon imageIcon = new ImageIcon(img);
		imagejLabel.setIcon(imageIcon);
	}
	
	void negative_ActionPerformed(java.awt.event.ActionEvent event)
	{
		System.out.println("negative performing!");
		BackupImage = new ImageData(ActiveImage.rows,ActiveImage.cols, ActiveImage, 0,0); // set the BackupImage
		undo.setEnabled(true); // enable the undo button
		
		// perform the negate operation
		ActiveImage.negateImage();
		
		// update the display
		img = ActiveImage.createImage();
		ImageIcon imageIcon = new ImageIcon(img);
		imagejLabel.setIcon(imageIcon);
	}
	
	void edgeDetect_ActionPerformed(java.awt.event.ActionEvent event)
	{
		System.out.println("edgedetect performing!");
		BackupImage = new ImageData(ActiveImage.rows,ActiveImage.cols, ActiveImage, 0,0); // set the BackupImage
		undo.setEnabled(true); // enable the undo button
		
		// perform the edge detect operation
		ActiveImage.edgeDetect();
		
		// update the display
		img = ActiveImage.createImage();
		ImageIcon imageIcon = new ImageIcon(img);
		imagejLabel.setIcon(imageIcon);
	}
	
	void contrastStretch_ActionPerformed(java.awt.event.ActionEvent event)
	{
		System.out.println("contrast stretch performing!");
		BackupImage = new ImageData(ActiveImage.rows,ActiveImage.cols, ActiveImage, 0,0); // set the BackupImage
		undo.setEnabled(true); // enable the undo button
		
		// Set image as greyscale if it is not already
		ActiveImage.toGreyScale();
		
		// Display the current min and max 
		// intensities for the image
		int min,max;
		min = ActiveImage.getMinValue();
		max = ActiveImage.getMaxValue();
		JOptionPane.showMessageDialog(this, "Current Intensity Range: " + min + " - " + max);
		
		// prompt for the new min value
		int newmin;
		do{
			String diaInput = JOptionPane.showInputDialog(
					this,
					"Please enter new min value(0-255)\n",
					"Customized Dialog",
					JOptionPane.PLAIN_MESSAGE);
			try{
				newmin = Integer.parseInt(diaInput);
			}catch(NumberFormatException e)
			{
				newmin = -1; // will fail the while and re-prompt user
			}
		}while(!(0 <= newmin && newmin <= 255));

		// prompt for the new max value
		int newmax;
		do{
			String diaInput = JOptionPane.showInputDialog(
					this,
					"Please enter new max value(0-255)\n",
					"Customized Dialog",
					JOptionPane.PLAIN_MESSAGE);
			try{
				newmax = Integer.parseInt(diaInput);
			}catch(NumberFormatException e)
			{
				newmax = -1; // will fail the while and re-prompt user
			}
		}while(!(0 <= newmax && newmax <= 255));
		
		// perform the contrast stretch operation
		ActiveImage.contrastStretch(newmin, newmax);
		
		// update the display
		img = ActiveImage.createImage();
		ImageIcon imageIcon = new ImageIcon(img);
		imagejLabel.setIcon(imageIcon);
	}

	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
		}
	}
	

	void ImageFrame_WindowOpened(java.awt.event.WindowEvent event)
	{
		// to do: code goes here.
	}
}
