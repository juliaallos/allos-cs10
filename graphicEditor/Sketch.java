import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Julia Allos Dartmouth CS 10, Spring 2024
 */
public class Sketch {
    Map<Integer, Shape> listOfShapes;

    //constructor
    public Sketch() {
        listOfShapes = new HashMap<Integer, Shape>();
    }

    //adds shape to map
    public void addShape(int id, Shape s) {
        listOfShapes.put(id, s);
    }

    //returns map
    public synchronized Map<Integer, Shape> getListOfShapes() {
        return listOfShapes;
    }

    //returns the id of a shape if a point (x,y) is pressed in a shape
    //returns -1 otherwise
    public int containsId(int x, int y) {
        for(int i: listOfShapes.keySet()) {
            if(listOfShapes.get(i).contains(x,y)) {
                return i;
            }
        }
        return -1;
    }

    //toString() method
    public String toString() {
        String listString = "";
        for(int i: getListOfShapes().keySet()) {
            if(i == getListOfShapes().size()-1) listString += getListOfShapes().get(i);
            else listString += getListOfShapes().get(i) + "\n";
        }
        return listString;
    }

    //draws
    public synchronized void draw(Graphics g) {
        for(Integer s: getListOfShapes().keySet()){
            getListOfShapes().get(s).draw(g);
        }
    }

    //method to help shift map with deletion
    public void removeShape(int i) {
        listOfShapes.remove(i);
        for(int j = i+1; j<listOfShapes.size()+1; j++) {
            Shape tempShape = listOfShapes.remove(j);
            if(tempShape != null) {
                listOfShapes.put(j-1, tempShape);
            }
        }
    }


}
