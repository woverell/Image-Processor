import java.awt.*;
import java.io.*;
import java.awt.image.*;


/**

Class ImageData is a base class which
respresents image data and the methods for
producing the corresponding wavelet image,
as well as methods to access both of these
datas. </p>

@author L. Grewe
@version 0.0a Feb. 1999
*/

//Note: extends Component to inherit its createImage() method
class ImageData extends Component
{    boolean verbose = false;

     //File where data stored and format
     String filename = "";
     String format   = "";

 
     // Num Rows, columns
     public int rows=0, cols=0;

     //image data
     public int data[];
     int greyscale = 1; // 0 if greyscale, 1 if color
     public float minDataRange = Float.MAX_VALUE;
     public float maxDataRange = Float.MIN_VALUE;





    //**METHODS: for image data*/
     int getData(int row, int col)
      { if (row < rows && col <cols )
            return data[(row*cols)+col];
        else
            return 0;
      }


      int getDataForDisplay(int row, int col)
      {   if (row < rows && col <cols )
            return data[(row*cols)+col];
        else
            return 0;
      }


      void setData(int row, int col, int value)
      {  data[(row*cols)+col] = (int) value;

      }



     

  /**
   * Constructs a ImageData object using the
   * specified by an instance of java.awt.Image,
   * format, and size indicated by numberRows and
   * numberColumns.
   * @param img an Image object containing the data.
   * @param DataFormat the format of the data
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   * @exception IOException if there is an error during
   *  reading of the rangeDataFile.
   */
   public ImageData(Image img, String DataFormat,
                    int numberRows, int numberColumns) throws IOException
     {
      int pixel, red, green, blue, r,c;
      format = DataFormat;
      rows = numberRows;
      cols = numberColumns;
      PixelGrabber pg;

      //From the image passed retrieve the pixels by
      //creating a pixelgrabber and dump pixels
      //into the data[] array.
      data = new int[rows*cols];
      pg = new PixelGrabber(img, 0, 0, cols, rows, data, 0, cols);
      try {
          pg.grabPixels();
      } catch (InterruptedException e) {
          System.err.println("interrupted waiting for pixels!");
          return;
      }
      
      
      //Convert the PixelGrabber pixels to greyscale
      // from the {Alpha, Red, Green, Blue} format 
      // PixelGrabber uses.
      /**
      for(r=0; r<rows; r++)
      for(c=0; c<cols; c++)
        {   pixel = data[r*cols + c];
	        red   = (pixel >> 16) & 0xff;
            green = (pixel >>  8) & 0xff;
            blue  = (pixel      ) & 0xff;
            if(verbose)
                System.out.println("RGB: " + red + "," + green +"," +blue);
            data[r*cols+c] = (int)((red+green+blue)/3);
            if(verbose)
                System.out.println("Pixel: " + (int)((red+green+blue)/3));
            minDataRange = Math.min(minDataRange, data[r*cols+c]);
            maxDataRange = Math.max(maxDataRange, data[r*cols+c]);
        }                
	    **/
            
            
     
		//{{INIT_CONTROLS
		setBackground(java.awt.Color.white);
		setSize(0,0);
		//}}
	}



  /**
   * Constructs a ImageData object using the
   * specified  size indicated by
   * numberRows and numberColumns that is EMPTY.
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   */
   public ImageData(int numberRows, int numberColumns){

      rows = numberRows;
      cols = numberColumns;
      
     

   }
   
   
   
   /**
   * Constructs a ImageData object using the
   * specified  size indicated by
   * numberRows and numberColumns.  Fill the data[]
   * array with the information stored in
   * the ImageData instance ID, from the 2D
   * neighborhood starting at the upper-left coordinate
   * (rStart,cStart) 
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   * @param ID image data to copy data from
   * @param rStart,cStart  Start of Neighborhood copy
   */
   public ImageData(int numberRows, int numberColumns, ImageData ID,
                    int rStart,int cStart){


      //saftey check: Retrieval in ID outside of boundaries
      if(ID.rows<(rStart+numberRows) || ID.cols<(cStart+numberColumns))
      {  rows = 0;
         cols = 0;
         return;
      }   
      
      
      rows = numberRows;
      cols = numberColumns;
      
      //create data[] array.
      data = new int[rows*cols];
      int t;
      //Copy data from ID.
      for(int i=0; i<rows; i++)
      for(int j=0; j<cols; j++)
        {   
    	  if(ID.greyscale == 1) // if color
    	  {
    		  data[i*cols+j] = ID.data[(rStart+i)*ID.cols + j + cStart];
    	  }else // if greyscale
    	  {
    		  t = ID.data[(rStart+i)*ID.cols + j + cStart];
    		  data[i*cols+j] = (255 << 24) | (t << 16) | (t << 8) | t;
    	  }
            //minDataRange = Math.min(minDataRange, data[i*cols+j]);
            //maxDataRange = Math.max(maxDataRange, data[i*cols+j]);
        }    
      
      
   }   



//METHODS

  /**
   * creates a java.awt.Image from the pixels stored 
   * in the array data using 
   * java.awt.image.MemoryImageSource
   */
  public Image createImage()
   {
        int pixels[], t;
        pixels = new int[rows*cols];
    
        //translate the data in data[] to format needed
        for(int r=0;r<rows; r++)
        for(int c=0;c<cols; c++)
        {  t = data[r*cols + c];
        
         	if(greyscale == 0){
         		if(t == 999) //due to reg. transformation boundaries produced
         		{ t = 0; }  // see Transform.ApplyToImage() method
         		if(t<0) //due to processing
         		{ t = -t; }
         		else if(t>255) //due to processing
         		{ t = 255; }
            
         		pixels[r*cols+c] = (255 << 24) | (t << 16) | (t << 8) | t;
           }else{
        	//Now for color, still need to fix errors due to processing
        	pixels[r*cols+c] = t;
           }
        }
    
        //Now create Image using new MemoryImageSource
        return ( super.createImage(new MemoryImageSource(cols, rows, pixels, 0, cols)));
	
   } 
   
  	// convert the image data to greyscale
  	// used in edge detect and contrast stretch
  	public void toGreyScale()
  	{
  		if(greyscale == 1) // if image is color
  		{
  			int pixel, r, g, b;
  			
  			// loop through every pixel and split into rgb components
  			// then just average the intensities: (r+g+b)/3
  			for(int row=0; row<rows; row++)
  				for(int c=0; c<cols; c++)
  				{   
  					pixel = data[row*cols + c];
  					r   = (pixel >> 16) & 0xff;
  					g = (pixel >>  8) & 0xff;
  					b  = (pixel      ) & 0xff;

  					data[row*cols+c] = (int)((r+g+b)/3);  
  					minDataRange = Math.min(minDataRange, data[row*cols+c]);
  					maxDataRange = Math.max(maxDataRange, data[row*cols+c]);
  				}
  			greyscale = 0;
  		}
  	}
  	
  	public int getMinValue()
  	{
  		return (int)minDataRange;
  	}
  	
  	public int getMaxValue()
  	{
  		return (int)maxDataRange;
  	}
   
   /**
	 *Stores the data image to a 
	 * a file as COLOR raw image data format
	 */
	public void storeImage(String filename)throws IOException
	{ 
	   
	    int  pixel, alpha, red, green,blue;
	    
	    
	        
        //Open up file	
        FileOutputStream file_output = new FileOutputStream(filename);
        DataOutputStream DO = new DataOutputStream(file_output);
 
 
        //Write out each pixel as integers
        
	
         
        for(int r=0; r<rows; r++)
	    for(int c=0; c<cols; c++) {
            pixel = data[r*cols + c];
	        red = pixel;
            green = pixel;
            blue = pixel;
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
   
	// Negates the ActiveImage
	public void negateImage()
	{

		if(greyscale == 0)
		{
			for(int row=0;row<rows; row++)
				for(int c=0;c<cols; c++)
				{
					data[row*cols+c] = -data[row*cols+c] + 255;
				}
		}else
		{
			// Have pixel data stored in data array
			// Need to split each pixel into its rbg
			// components, then perform negative
			// operation on each color value, then
			// recombine colors into pixel
			int r, g, b;

			for(int row=0;row<rows; row++)
				for(int c=0;c<cols; c++)
				{
					// split current pixel into rgb components
					r = (data[row*cols + c] & 0xff0000) >> 16;
					g = (data[row*cols + c] & 0x00ff00) >> 8;
	    			b = (data[row*cols + c] & 0x0000ff);

	    			// perform negative operation
	    			r = -r + 255;
	    			g = -g + 255;
	    			b = -b + 255;

	    			// put back into data array
	    			data[row*cols + c] = (255 << 24) | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
				}
		}


	}
	
	private int thresholdInt(int i, int tvalue)
	{
		if(i > tvalue)
		{
			return 255;
		}else
		{
			return 0;
		}
	}
	
	// Performs a threshold of the ActiveImage with
	// provided threshold value: tvalue
	public void thresholdImage(int tvalue)
	{
		// Have pixel data stored in data array
		// Need to split each pixel into rgb
		// components, then perform the threshold
		// on each component separately, then recombine
		// and store back into data array
		
		int r, g, b;
		
		for(int row=0;row<rows; row++)
	    for(int c=0;c<cols; c++)
	    {
	    	if(greyscale == 0){
	    		data[row*cols + c] = thresholdInt(data[row*cols + c], tvalue);
	    	}else{
	    		// split current pixel into rgb components
	    		r = (data[row*cols + c] & 0xff0000) >> 16;
	    		g = (data[row*cols + c] & 0x00ff00) >> 8;
	    		b = (data[row*cols + c] & 0x0000ff);
	        
	    		// perform threshold operation
	    		r = thresholdInt(r, tvalue);
	    		g = thresholdInt(g, tvalue);
	    		b = thresholdInt(b, tvalue);
	        
	    		// put back into data array
	    		data[row*cols + c] = (255 << 24) | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	    	}
	    }
	}
   
	// Performs an edge detection on the image
	// converts image data to greyscale
	// Uses Sobel edge detection
	public void edgeDetect()
	{
		if(greyscale == 1)
		{
			toGreyScale(); // convert to greyscale
		}
		
		// filters for sobel 
		int Hx[] = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
		int Hy[] = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
		
		// copy the data so we can pull values from
		// this copy and modify the original image
		// with the edge values
		int[] datacopy = new int[data.length];
		System.arraycopy(data, 0, datacopy, 0, data.length);
		
		int dxvalue, dyvalue;
		
		// loop through pixels except for border pixels
		for(int row=1;row<rows - 1; row++)
		for(int c=1;c<cols - 1; c++)
		{
			// Calculate the convolutions for x and y directions
			dxvalue = Hx[0] * datacopy[(row - 1) * cols + c - 1] + Hx[1] * datacopy[(row - 1) * cols + c] + 
					Hx[2] * datacopy[(row - 1) * cols + c + 1] + Hx[3] * datacopy[(row) * cols + c - 1] + 
					Hx[4] * datacopy[(row) * cols + c] + Hx[5] * datacopy[(row) * cols + c + 1] + 
					Hx[6] * datacopy[(row + 1) * cols + c - 1] + Hx[7] * datacopy[(row + 1) * cols + c] + 
					Hx[8] * datacopy[(row + 1) * cols + c + 1];

			dyvalue = Hy[0] * datacopy[(row - 1) * cols + c - 1] + Hy[1] * datacopy[(row - 1) * cols + c] + 
					Hy[2] * datacopy[(row - 1) * cols + c + 1] + Hy[3] * datacopy[(row) * cols + c - 1] + 
					Hy[4] * datacopy[(row) * cols + c] + Hy[5] * datacopy[(row) * cols + c + 1] + 
					Hy[6] * datacopy[(row + 1) * cols + c - 1] + Hy[7] * datacopy[(row + 1) * cols + c] + 
					Hy[8] * datacopy[(row + 1) * cols + c + 1];
			
			// Then take square root of the sum of the squares
			// And set as new pixel value
			data[row*cols + c] = (int)Math.sqrt((dxvalue*dxvalue)+(dyvalue+dyvalue));
		}
	}
	
	// Returns the contrast of the image as a percent
	private int contrastOfImage()
	{
		int contrast = (int)((maxDataRange - minDataRange)/255*100);
		System.out.println("Contrast is " + contrast + " percent!");
		return contrast;
	}
	
	// Stretch the contrast of the image by
	// percent: percentToIncrease
	public void contrastStretch(int newMin, int newMax)
	{
		// First convert the image to greyscale
		// which also sets the max and min intensities
		// minDataRange maxDataRange
		if(greyscale == 1)
		{
			toGreyScale();
		}
		
		// Stretch the contrast to newMin, newMax
		// First calculate the multiplier( newMax-newMin / max-min )
		// Then subtract min from each pixel, multiply by this value
		// and add newMin to this value
		float multiplier = (newMax - newMin)/(maxDataRange-minDataRange);
		
		for(int row=1;row<rows - 1; row++)
		for(int c=1;c<cols - 1; c++)
		{
			data[row*cols + c] = (int)((data[row*cols + c] - minDataRange)*multiplier+newMin);
		}
		maxDataRange = newMax;
		minDataRange = newMin;
		
	}
     
}//End ImageData
