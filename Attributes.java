/*                                        
***************************************************                                              
  Author: Adriano Alves
  Date  : 12/09/15                               
  Program Name: Attributes.java                  
  Objective: file needed to work with HW6 
             DrawingBoard.class
****************************************************                                              
*/                                        
                                              
import java.awt.*;                      
import java.io.*;

class Attributes implements Serializable
{
    private Point POINT,POINT_PRESSED,POINT_RELEASED;
    private String BRUSH,CMD;
    private Color COLOR;
    private boolean FILLED, EREASED;
    
// constructors
    public Attributes(){}
    public Attributes(Point point, Point pressed, Point released, Color color,
                      String brush, String cmd, boolean filled)
    {
        POINT = point;
        POINT_PRESSED = pressed;
        POINT_RELEASED = released;
        COLOR = color;
        BRUSH = brush;
        CMD = cmd;
        FILLED = filled;
    }
    //********   gethers   **********
    public Point getPoint() { return POINT; }
    public Point getPointPressed() { return POINT_PRESSED; }
    public Point getPointReleased() { return POINT_RELEASED;}
    public Color getMyColor() { return COLOR; }
    public String getBrush() { return BRUSH; }
    public String getCmd() { return CMD; }
    public boolean isFilled() { return FILLED; }
}
