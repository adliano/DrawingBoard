/*
************************************************** 
 Author: Adriano Alves
 Date  : 12/01/15
 Program Name: DrawingBoard.java
 Objective: HW6 CS211S
            This program make draws and
            save it to a file.
           This program needs file Attributes.class

 notes: - tempColor will hold selected color when user 
          use erease ou if user do not selects new color 
        - strFormat its used to do not set cmd with 
          no toggleButton commands when clicked
        - program will create a temp.der file to save 
          draws when frame is Iconified and file will be 
          removed after exit
*************************************************** 
*/
 
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.imageio.*;

public class DrawingBoard
{
    JFrame frame;
    JToggleButton JTB;
    ButtonGroup bg = new ButtonGroup();
    JButton JB;
    private JPanel southPanel, northPanel;
    Draws d = new Draws();
    Point myPoint, pointPressed, pointReleased;
    Color tempColor, color = Color.BLACK;// DEFAULT BLACK
    boolean isFilled = false;
    boolean savedFlag = false;
    String brush ="*",strFileName, cmd = "BRUSH",strFormat=cmd;
    String tgbs[]={"RECTANGLE","CIRCLE","LINE","BRUSH"};
    String bs[]={"SAVE","LOAD","COLOR","EXIT","CLEAR","UNDO","HELP"};
    ArrayList<Attributes> attributes = new ArrayList<Attributes>();
    //HashMap<String,JToggleButton> toggles = new HashMap<>();
    //HashMap<String,JButton> buttons = new HashMap<>();

    /****** constructor ******/
    public DrawingBoard()
    {
       color = Color.BLACK;
       setButtons();
       d.setBrush(brush);
       d.addMouseListener(mickey);
       d.addMouseMotionListener(mickey);
       d.addComponentListener(componentadapter);

       frame = new JFrame("Drawing Board");
       //frame.setResizable(false);// i dont like it!
       frame.setLayout(new BorderLayout());
       frame.setSize(800, 700);
       //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
       frame.addWindowListener(window);
       frame.add(southPanel ,BorderLayout.NORTH);
       frame.add(d ,BorderLayout.CENTER);
       frame.add(northPanel ,BorderLayout.SOUTH);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
    }
    /********** MOUSE ADAPTER ***********/
    MouseAdapter mickey = new MouseAdapter()
    {
       Point pzero = new Point(0,0);
       /************* mouseDragged() *************/
       @Override
       public void mouseDragged(MouseEvent me)
       {
          debug("mouse dragged");
          if(SwingUtilities.isLeftMouseButton(me)) 
          {
             tempColor = color;
          }
          if(SwingUtilities.isRightMouseButton(me))
          {
             tempColor = d.getEreaseColor();
          }

          d.setMyColor(tempColor);
          debug("Color mouse dargged: "+tempColor.toString());
          myPoint = me.getPoint();
          debug("myPoint mouse dragged: "+myPoint);
          d.setMyPoint(myPoint);
          debug("BUSH mouse dragged: "+brush);
          d.setBrush(brush);
          d.setCMD(strFormat);
          debug("strFormat mouse dragged: "+strFormat);
          if(strFormat.equals("BRUSH"))
          {
             attributes.add(new Attributes(myPoint,pzero,pzero,tempColor,
                                           brush,strFormat,false));
             debug("attributes size : "+attributes.size());
             d.mkDraws();
          }
      }
      /************** mousePressed() **************/
       @Override
       public void mousePressed(MouseEvent me)
       {
          savedFlag = false;
          debug("mouse pressed "+me.getClickCount());
          if(me.getClickCount() == 2) isFilled = true;
          else isFilled = false;
          pointPressed = me.getPoint();
          debug("pressed at "+pointPressed.x+" "+pointPressed.y);
          debug("mouse double preesed :"+isFilled);
       }
       /************* mouseReleased() ***************/
       @Override
       public void mouseReleased(MouseEvent me)
       {
          savedFlag = false;
          debug("mouse released "+me.getClickCount());
          if(SwingUtilities.isLeftMouseButton(me))
          {
             tempColor = color;
             debug("Color Right Button Released:"+tempColor);
          }
          if(SwingUtilities.isRightMouseButton(me))
          {
             tempColor = d.getEreaseColor();
             debug("Color left Button Released:"+tempColor);
          }
          if(!strFormat.equals("BRUSH"))
          {
             d.setMyColor(tempColor);
             pointReleased = me.getPoint();
             debug("pressed at "+pointPressed.x+" "+pointPressed.y);
             debug("released at "+pointReleased.x+" "+pointReleased.y);
             d.setMyPoint(myPoint);
             d.setPointPressed(pointPressed);
             d.setPointReleased(pointReleased);
             d.setBrush(brush);
             d.setCMD(strFormat);
             debug("FORMAT :"+strFormat);
             d.setFilled(isFilled);
             attributes.add(new Attributes(myPoint,pointPressed,pointReleased,
                            tempColor,brush,strFormat,isFilled));
             debug("attributes size : "+attributes.size());
             d.mkDraws();
          }
          isFilled = false;
          debug("isFilled: "+isFilled);
       }
    };
   /************ ACTION LISTENER ************/
   ActionListener buttonActs = new ActionListener()
   {
       /******* ActiomPerformed *********/
       @Override
       @SuppressWarnings("unchecked")
       public void actionPerformed(ActionEvent e)
       {
           cmd = e.getActionCommand();
           //Object object = e.getSource();
           switch(cmd)
           {
               case "BRUSH":
                   strFormat = cmd;
                   String tempBrush = brush;
                   brush = JOptionPane.showInputDialog("** Select you brush **");
                   if(brush == null) brush = tempBrush;
                   break;
               case "SAVE":
                   debug("SAVE TO FILE"); 
                   debug("attributes[] before save:"+attributes.size());
                   strFileName = getFileName2Save(frame);
                   if(strFileName != null)
                   {
                      debug("File Choosed");
                      serialize(strFileName, attributes);
                      savedFlag = true;
                   }
                   debug("attributes[] after save:"+attributes.size());
                   break;
               case "LOAD":
                   debug("LOAD FROM FILE");
                   attributes.clear();
                   debug("attributes[] before load:"+attributes.size());
                   strFileName = getSavedFileName(frame);
                   if(strFileName != null)
                   {
                      attributes=(ArrayList<Attributes>)deserialize(strFileName);
                   }
                   debug("attributes[] after load:"+attributes.size());
                   int i = 1;// debug
                   for(Attributes a : attributes)
                   {
                      debug("item loaded and painted"+i++);
                      d.setMyPoint(a.getPoint());
                      d.setPointPressed(a.getPointPressed());
                      d.setPointReleased(a.getPointReleased());
                      d.setMyColor(a.getMyColor());
                      d.setBrush(a.getBrush());
                      d.setCMD(a.getCmd());
                      d.setFilled(a.isFilled());
                      d.mkDraws();
                   }
                   break;
               case "COLOR":
                   debug("SET COLOR");
                   color = JColorChooser.showDialog(frame,
                           "Choose Color", Color.black);
                   if(color == null) color = tempColor;
                   break;
               case "EXIT":
                   debug("EXIT PROGRAM");
                   String msg = "<html><h1><b>EXIT PROGRAM WITHOUT SAVE ?</h1></html>";
                   String title = "****** EXIT ******";
                   if(savedFlag || attributes.size() == 0 ||isConfirm(msg,title)) bye();;
                   break;
               case "CLEAR":
                   // if no draw on canvas no need ask on exit 
                   savedFlag = true;
                   attributes.clear();
                   d.cls();
                   break;
               case "UNDO":
                   debug("UNDO");
                   if(attributes.size() != 0)
                   {
                      savedFlag = false;
                      int lastIndex = (attributes.size())-1;
                      attributes.remove(lastIndex);
                      d.cls();
                      for(Attributes temp : attributes)
                      {
                         d.setMyPoint(temp.getPoint());
                         d.setPointPressed(temp.getPointPressed());
                         d.setPointReleased(temp.getPointReleased());
                         d.setMyColor(temp.getMyColor());
                         d.setBrush(temp.getBrush());
                         d.setCMD(temp.getCmd());
                         d.setFilled(temp.isFilled());
                         d.mkDraws();
                      }
                   }
                   break;
               case "HELP":
                   JOptionPane.showMessageDialog(null,getHelpPanel(),
                                     "**** HELP MENU ****",JOptionPane.PLAIN_MESSAGE);
                   break;
               default :
                   strFormat = cmd;
                   break;
           }
           debug("strFormat at action listener:"+strFormat);
       }
    };
    /****************** WindowAdapter ******************/
    WindowAdapter window = new WindowAdapter()
    {
       /**** windowClosing() ****/
       @Override
       public void windowClosing(WindowEvent we)
       {
          debug("ASK BEFORE CLOSE");
          String msg = "<html><h1><b>EXIT PROGRAM WITHOUT SAVE ?</h1></html>";
          String title = "****** EXIT ******";
          if(savedFlag || attributes.size() == 0 ||isConfirm(msg,title)) bye();
       }
       /**************** windowDeiconified *************/
       @Override
       @SuppressWarnings("unchecked")
       public void windowDeiconified(WindowEvent e)
       {
          debug("WINDOWS Deiconified");
          attributes.clear();
          attributes=(ArrayList<Attributes>)deserialize("temp.der");
             debug("Attributes size after Deiconified:"+attributes.size());
          for(Attributes a : attributes)
          {
             d.setMyPoint(a.getPoint());
             d.setPointPressed(a.getPointPressed());
             d.setPointReleased(a.getPointReleased());
             d.setMyColor(a.getMyColor());
             d.setBrush(a.getBrush());
             d.setCMD(a.getCmd());
             d.setFilled(a.isFilled());
             d.mkDraws();
          }
       }
       /***************** windowIconified() ************/
       @Override
       public void windowIconified(WindowEvent e)
       {
          debug("WINDOWS Iconified");
          serialize("temp.der", attributes);
          attributes.clear();
          debug("Attributes size after Iconified:"+attributes.size());
       }
    };
    /**************** Component Adapter ***************/
    ComponentAdapter componentadapter = new ComponentAdapter()
    {
       /******** componentResized() ********/          
       @Override
       @SuppressWarnings("unchecked")
       public void componentResized(ComponentEvent ce)
       {
          debug("frame resized");
          for(Attributes a : attributes)
          {
             d.setMyPoint(a.getPoint());
             d.setPointPressed(a.getPointPressed());
             d.setPointReleased(a.getPointReleased());
             d.setMyColor(a.getMyColor());
             d.setBrush(a.getBrush());
             d.setCMD(a.getCmd());
             d.setFilled(a.isFilled());
             d.mkDraws();
          }
       }
       /*********** componentShow() *********/
       @Override
       public void componentShown(ComponentEvent ce)
       {
       }
    };
    /****************** setButtons() ******************/
    public void setButtons()
    {
       // UPPER PANEL WITH TOGLEBUTTONS
       southPanel = new JPanel(new GridLayout(1,5,10,10));
       southPanel.setBackground(Color.DARK_GRAY);
       southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
       for(String cmd : tgbs)
       {
          // if fill and draw set bgFill
          JTB = new JToggleButton("<html><h3><b>"+cmd+"</h3></html>");
          JTB.setActionCommand(cmd);
          JTB.addActionListener(buttonActs);
          JTB.setToolTipText(toggleTip(cmd));
          bg.add(JTB);
          southPanel.add(JTB);
          //Default Toggle Button
          if(cmd.equals("BRUSH")) JTB.setSelected(true);
          //toggles.put(cmd,JTB);
       }
       //LOWER PANEL WITH BUTTONS
       northPanel = new JPanel(new GridLayout(1,8,10,5));
       northPanel.setBackground(Color.DARK_GRAY);
       northPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
       for(String cmd : bs)
       {
          JB = new JButton("<html><h3><b>"+cmd+"</h3></html>");
          JB.setActionCommand(cmd);
          JB.addActionListener(buttonActs);
          JB.setToolTipText(buttonTip(cmd));
          northPanel.add(JB);
          //buttons.put(cmd,JB);
       }
    }
    /*************** serialize() **********************/
    public void serialize(String fileName, Object obj)
    {
       //default file name
       if(fileName == null) fileName = "untitled.der";
       try(ObjectOutputStream oos = new ObjectOutputStream(
                                   new FileOutputStream(fileName)))
       {
           oos.writeObject(obj);
       }catch(IOException e){System.err.println("error:" + e);}
    }
    /*************** deserialize() *******************/
    public Object deserialize(String fileName)
    {
       //default file name
       if(fileName == null) fileName = "untitled.der";
       Object myObject = null;
       try(ObjectInputStream ois = new ObjectInputStream(
                                   new FileInputStream(fileName)))
       {
           myObject = ois.readObject();
       }catch(Exception e){System.err.println("error:"+e);}
       return(myObject);
    }
    /************* isConfirm() ******************/
    public boolean isConfirm(String msg,String title)
    {
       int option = JOptionPane.YES_NO_OPTION;
       int yes = JOptionPane.YES_OPTION;
       int no = JOptionPane.NO_OPTION;
       int result = JOptionPane.showConfirmDialog(null,msg,title,option);
 
       return( result == yes ); 
    }
    /************** getFileName2Save() ***************/
    public String getFileName2Save(JFrame frame)
    {
       String fileName = "untitled.der"; // DEFAULT
       JFileChooser fileChooser = new JFileChooser();
       fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
       fileChooser.setFileFilter(new FileNameExtensionFilter("DATA FILES (*.der)","der"));
       fileChooser.setSelectedFile(new File(fileName));
       fileChooser.setCurrentDirectory(new File("."));
       fileChooser.setDialogTitle("******* SELECT FILE *******");

       int value = fileChooser.showSaveDialog(frame);
       if(value == JFileChooser.APPROVE_OPTION)
       {
         fileName = fileChooser.getSelectedFile().getName(); 
       }
       else fileName = null;

       return fileName;
    }
    /************** getSavedFileName() ***********/
    public String getSavedFileName(JFrame frame)
    {
       String fileName = null ;
       JFileChooser fileChooser = new JFileChooser();
       fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
       fileChooser.setFileFilter(new FileNameExtensionFilter("DATA FILES (*.der)","der"));
       fileChooser.setCurrentDirectory(new File("."));
       
       int value = fileChooser.showOpenDialog(frame);
       if(value == JFileChooser.APPROVE_OPTION)
       {
          fileName = fileChooser.getSelectedFile().getName();
       }
       return fileName;
    }
    /************** getSelectedColor() *************/
    public Color getSelectedColor()
    {
       JColorChooser jcc = new JColorChooser(Color.black);
       return Color.white;
    }
    /****************** toggleTip() *******************/
    public static String toggleTip(String toggleName)
    {
        return("<html><body style=\"color:yellow;background-color:black\">"+
               "<p><b>draw "+toggleName+" click help for "+
               "more info </p></body></html>");
    }
    /****************** buttonTip() ******************/
    public static String buttonTip(String buttonName)
    {
        return("<html><body style=\"color:yellow;background-color:black\">"+
               "<p><b>click for "+buttonName+"</p></body></html>");
    }
    /********* getHelpPanel() **********/
    public JPanel getHelpPanel()
    {
        JPanel panel = new JPanel();
        ImageIcon image = new ImageIcon("help.png");
        JLabel label = new JLabel(help());
        label.setIcon(image);
        panel.add(label);

        return panel;
    }
    /************** help() ****************/
    public String help()
    {
       return("<html><body style=\"background-color:rgb(0,228,228)\">"+
              "<center>"+
              "<h1 style=\"color:navy\">"+
              "<b> Program to make draws </h1>"+
              "<p> Select style that you want to draw </p>"+
              "<p> Use mouse's LEFT button to draw and </p>"+
              "<p> RIGHT button to erease </p>"+
              "<p> couble click to draw filled</p>"+
              "<br/><br/>"+
              "<h3><b> made by Adriano Alves <br/></h3></body></html>");
    }
    /*************** println() ********************/
    public static void Xprintln(Object o)
    {
        System.out.println(""+o);
    }
    /*************** debug() *********************/
    public void debug(Object o)
    {
       if (System.getProperty("DEBUG") != null)
       {
           System.out.println(""+o);
       }
    }
    /***************** bye() *******************/
    public void bye()
    {
       try
       {
          debug("******* deleting file temp.der *****");
          ProcessBuilder pb = new ProcessBuilder("rm", "temp.der");
          pb.start();
       }catch(Exception e){System.err.println("error at bye()");}
       System.exit(0);
    }
/*** =============== inner classes ================= ***/
class Draws extends Canvas
{
    Point POINT, POINT_PRESSED,POINT_RELEASED;
    String BRUSH = "*";
    String CMD = "BRUSH";
    Color MYCOLOR;
    boolean IS_FILLED;

   /******** setMyPoiny() ******/
   public void setMyPoint(Point point)
   {
      POINT = point;
   }
   /********* setPointPressed() ******/
   public void setPointPressed(Point pressed)
   {
      POINT_PRESSED = pressed;
   }
   /*********** setPoitReleased() ********/
   public void setPointReleased(Point released)
   {
      POINT_RELEASED = released;
   }
   /******** setMyColor *******/
    public void setMyColor(Color color)
    {
       MYCOLOR = color;
    }
    /********* setBrush() *********/
    public void setBrush(String brush)
    {
       BRUSH = brush;
    }
   /********* setCMD() *********/
   public void setCMD(String cmd)
   {
      CMD = cmd;
   }
   /********* setFilled *******/
   public void setFilled(boolean filled)
   {
      IS_FILLED = filled;
   }
    /********* mkDraws() *********/
    public void mkDraws()
    {
       if(POINT != null)
       {
          switch(CMD)
          {
             case "BRUSH":
                myDrawBrush(POINT,BRUSH);
             break;
             case "CIRCLE":
                myDrawCircle(POINT_PRESSED,POINT_RELEASED,IS_FILLED);
             break;
             case "LINE":
                myDrawLine(POINT_PRESSED,POINT_RELEASED);
             break;
             case "RECTANGLE":
                 myDrawRect(POINT_PRESSED,POINT_RELEASED,IS_FILLED);
             break;
          }
       }
    }
    /********** myDrawBrush() *********/
    public void myDrawBrush(Point a,String brush)
    {
        Graphics g = getGraphics();
        g.setColor(MYCOLOR);
        g.drawString(brush,a.x,a.y);
        g.dispose();
    }
    /********** myDrawCircle() ********/
    public void myDrawCircle(Point a, Point b,boolean filled)
    {
        Graphics g = getGraphics();
        g.setColor(MYCOLOR);
        int w = Math.abs(b.x-a.x);
        int h = Math.abs(b.y-a.y);
        int x = Math.min(a.x,b.x);
        int y = Math.min(a.y,b.y);
        if(filled) g.fillOval(x, y, w, h);
        else g.drawOval(x, y, w, h);
        g.dispose();
    }
    /************ MyDrawRect() ***********/
    public void myDrawRect(Point a, Point b,boolean filled)
    {
        Graphics g = getGraphics();
        g.setColor(MYCOLOR);
        debug("myDrawRect poit a:"+a);
        debug("myDrawRect poit b:"+b);
        int w = Math.abs(b.x-a.x);
        debug("vlue of w:"+w);
        int h = Math.abs(b.y-a.y);
        debug("vlue of h:"+h);
        int x = Math.min(a.x,b.x);
        debug("vlue of x:"+x);
        int y = Math.min(a.y,b.y);
        debug("vlue of y:"+y);
        if(filled) g.fillRect(x,y,w,h);
        else g.drawRect(x,y,w,h);
        g.dispose();
    }
    /************ myDrawLine() ***********/
    public void myDrawLine(Point a, Point b)
    {
        Graphics g = getGraphics();
        g.setColor(MYCOLOR);
        g.drawLine(a.x,a.y,b.x,b.y);
        g.dispose();
    }
    /*********** update() *************/
    public void update(Graphics g)
    {
       paint(g);
    }
    /*********** cls() ***************/
    public void cls()
    {
       Graphics g = getGraphics();
       BRUSH ="";
       g.setColor(getBackground());
       g.fillRect(0, 0, getWidth(), getHeight());
       g.setColor(getForeground());
       g.dispose();
    }
    /************** getEreaseColor() *********/
    public Color getEreaseColor()
    {
        return(getBackground());
    }
    /*************** paint() ***************/
    public void paint(Graphics g)
    {
    }
}
    /********** main() *********/
    public static void main(String args[])
    {
        javax.swing.SwingUtilities.invokeLater(()->new DrawingBoard());
    }
}
// ****** END ****** //
