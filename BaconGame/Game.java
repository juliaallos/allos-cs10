import java.util.List;
/*Julia Allos
* Game Interface- Problem set 4
* 9 May 2024
 */

public interface Game<V,E> {
    //what happens with user presses c
    public void keyC(int num);

    //what happens with user presses d
    public void keyD(int low, int high);

    //what happens with user presses i
    public void keyI();

    //what happens with user presses p
    public void keyP(V name);

    //what happens with user presses s
    public void keyS(int low, int high);

    //what happens with user presses u
    public void keyU(V name);


}
