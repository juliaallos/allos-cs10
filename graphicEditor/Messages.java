import java.awt.*;

/** @author Julia Allos Dartmouth CS 10, Spring 2024
 */
public class Messages {
    //updating sketch
    public static void handleMessage(Sketch sketch, String message) {
        String[] tokens = message.split(" ");
        String command = tokens[0];
        String shape = tokens[1];
        Shape curr = null;
        Color color;
        int id = 0;
        //adding
        if(command.equals("add")) {
            id = sketch.getListOfShapes().size();
            if(shape.equals("ellipse")) {
                color = new Color(Integer.parseInt(tokens[6]));
                curr = new Ellipse(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), color);
            }
            else if(shape.equals("segment")) {
                color = new Color(Integer.parseInt(tokens[6]));
                curr = new Segment(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), color);

            }
            else if(shape.equals("polyline")) {
                color = new Color(Integer.parseInt(tokens[tokens.length-1]));
                curr = new Polyline(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), color);
                for(int i=4; i<tokens.length-1; i = i+2) {
                    ((Polyline)curr).addPoints(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i+1]));
                }
            }
            else if(shape.equals("rectangle")) {
                color = new Color(Integer.parseInt(tokens[6]));
                curr = new Rectangle(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), color);
            }
        }
        //moving
        else if(command.equals("move")) {
            id = Integer.parseInt(tokens[1]);
            int x = Integer.parseInt(tokens[2]) - Integer.parseInt(tokens[4]);
            int y = Integer.parseInt(tokens[3]) - Integer.parseInt(tokens[5]);
            curr = sketch.getListOfShapes().get(Integer.parseInt(tokens[1]));
            curr.moveBy(x, y);
        }
        //recoloring
        else if(command.equals("recolor")) {
            id = Integer.parseInt(tokens[1]);
            curr = sketch.getListOfShapes().get(Integer.parseInt(tokens[1]));
            curr.setColor(new Color(Integer.parseInt(tokens[2])));

        }
        //deleting
        else if(command.equals("delete")) {
            id = Integer.parseInt(tokens[1]);
            sketch.removeShape(Integer.parseInt(tokens[1]));
        }
        //adding new/improved shape to sketch
        if(curr!= null && !command.equals("delete")) sketch.addShape(id, curr);

    }
}
