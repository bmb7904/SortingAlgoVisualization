package bernardi;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Graphical Visualization of 4 sorting algorithms: Selection sort, Quick Sort, Bubble
 * Sort, and Insertion Sort.
 *
 * The basic idea of this program is to illustrate what exactly happens to each element
 * in an array as the array is sorted, according to one of the four sorting algorithms
 * above. The array to be sorted will be represented as a collection of Rectangles,
 * whose heights represent their "value". Each time a radio button is clicked, an
 * arrayList of the specified number of Rectangles will be generated with random
 * "values" or heights. This application uses a custom class, Moves.
 * Objects of the Move class will store a "move", which is the location and index of
 * the array elements as they are swapped or moved during sorting. All of these sorting
 * algorithms utilize "swap", which will swap one element of an array with another.
 * These "swaps" are stored as Move objects in a queue to record them, and then displayed
 * later for illustration.
 *
 * In the QuickSort visualization, each time the partition method is called, the pivot
 * element is colored white, while each half of the partition is set to a randomly
 * generated, and different, color. I thought this was useful to visualize the
 * repetitive calls to the method partition() that is needed during QuickSort. After
 * all elements are sorted, they are all changed to white at the end.
 *
 * You might see random decimals hard-coded in the code to adjust for formatting of the
 * various Panes and Nodes. These are so the program will have the same look and
 * relative dimensions regardless of the screen resolution it is run on.
 *
 * This is purely for educational use. Anyone who possesses this is free to use, modify,
 * or distribute to any one for any reason.
 *
 *
 * @author Brett Bernardi
 */
public class SortingVisualization extends Application {
    final static double screenWidth = Screen.getPrimary().getBounds().getWidth();
    final static double screenHeight = Screen.getPrimary().getBounds().getHeight();

    // Using the height and width of the screen running the program, this calculates the
    // height and width of the app to take up 75% of the screen for every device running
    // it.
    final static int width = (int)(screenWidth * 0.75);
    // height of application
    final static int height = (int)(screenHeight * 0.75);
    static Queue<Moves> q; // A queue that stores moves made

    private static int n; // global number of elements to be sorted

    // The timeline should be declared global
    private static Timeline timeline = null;

    // Two identical ArrayLists of custom rectangle objects. These are both initialized
    // when the user selects the number of elements they wish to sort. The first
    // arraylist, listForSorting, will be sorted with the appropriate algorithm. The
    // moves made during the sorting will be captured and saved in the Moves queue.
    // Then the second arrayList listForVisualizing (which is unaltered at this point)
    // will be used along with the Moves queue to illustrate the moves.
    private static ArrayList<Rectangle> listForSorting;
    private static ArrayList<Rectangle> listForVisualizing;

    private static Button button = new Button();

    // Enum for the different sorting algorithms
    public enum AlgoType  {
        BUBBLE, INSERTION, SELECTION, QUICKSORT;
    }

    @Override
    public void start(Stage primaryStage) {

        Pane bottomPane = new Pane();
        FlowPane topPane = new FlowPane();
        topPane.setStyle("-fx-background-color: #21c6ef;");

        // declare and style button
        button.setStyle("-fx-background-color: RED; -fx-font-size: 30");
        button.setText("Start");
        // I calculated these decimals to work with whatever screen height and width it
        // is given, based on the screen running it.
        button.setPrefSize((int)(0.0586 * screenWidth),(int)(0.0521 * screenHeight));
        // set on action
        button.setOnAction(event -> toggle());
        // Make Start/Pause button disabled until a radio button is cilcked
        button.setDisable(true);

        topPane.setOnMousePressed(event -> {
        });

        bottomPane.setPrefSize(width, height);

        // creates radio buttons and adds them to a toggle group for mutually exclusivity
        RadioButton rbBubble = new RadioButton("Bubble Sort");
        RadioButton rbSelection = new RadioButton("Selection Sort");
        RadioButton rbInsertion = new RadioButton("Insertion Sort");
        RadioButton rbQuickSort = new RadioButton("QuickSort");

        rbBubble.setStyle("-fx-font-size: 25");

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbBubble, rbInsertion, rbQuickSort, rbSelection);

        topPane.setOrientation(Orientation.HORIZONTAL);
        topPane.setAlignment(Pos.CENTER);
        topPane.setPadding(new Insets((int)(0.00390625 * screenWidth)));
        topPane.setHgap((int)(0.02084 * screenHeight));

        // the root of the entire scene. Use VBox
        VBox root = new VBox();

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.showTickMarksProperty();
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setValue(50);
        slider.setStyle("-fx-font-size: 20;");


        Button creditsButton = new Button("Credits");
        creditsButton.setStyle("-fx-background-color: #5ea45e; -fx-font-size: 18");

        // This was added 9/28/2021, I found about about the Alert class in JavaFx,
        // after searching for a simple JOptionPane (from Swing) substitute.
        creditsButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setResizable(false);
            alert.setHeaderText("Author: Brett Bernardi");
            alert.setTitle("Credits");
            alert.show();
        });

        root.getChildren().add(topPane);
        root.getChildren().add(bottomPane);
        // Add all nodes to the top pane
        topPane.getChildren().addAll(slider, rbBubble, rbInsertion, rbSelection,
                rbQuickSort, button, creditsButton);
        root.setAlignment(Pos.CENTER);


        Scene scene = new Scene(root);
        bottomPane.setStyle("-fx-background-color: #000000;");


        // this is needed just to initialize the timeline object as something
        // otherwise will throw exception in buttonHandler
        timeline = new Timeline();

        n = (int)slider.getValue();


        /**
         * Custom Nested class that implements the EventHandler Interface. Instances
         * of this class are passed to each radio button.
         */
        class rbButtonHandler implements EventHandler<ActionEvent> {
            /**
             * Each time this handle method is called (clicking a radio button) will
             * create two new arrayLists of Rectangle objects of the specified number
             * in the slider.
             * @param event
             */
            @Override
            public void handle(ActionEvent event) {
                if(timeline.getStatus() == Animation.Status.RUNNING) {
                    timeline.stop();
                }
                if(button.isDisabled()) {
                    button.setDisable(false);
                }
                n = (int)slider.getValue();
                // Thank god for garbage collection
                listForSorting = null;
                listForVisualizing = null;
                listForSorting = new ArrayList<>(); // this list is the one used to sort
                listForVisualizing = new ArrayList<>(); // this list is used to display on Pane
                button.setText("Start");
                button.setDisable(false);
                createRectangles(listForSorting, listForVisualizing);
                initRectangles(listForVisualizing, bottomPane);


                if(rbBubble.isSelected()) {
                    bubbleSort(listForSorting);
                    setUpKeyFrame(AlgoType.BUBBLE);
                }
                else if(rbInsertion.isSelected()) {
                    insertionSort(listForSorting);
                    setUpKeyFrame(AlgoType.INSERTION);
                }
                else if(rbSelection.isSelected()) {
                    selectionSort(listForSorting);
                    setUpKeyFrame(AlgoType.SELECTION);
                }
                else {
                    qsort(listForSorting, 0, n-1);
                    setUpKeyFrame(AlgoType.QUICKSORT);
                }
            }
        }

        // pass buttonHandler into each radio button
        for(Toggle t: tg.getToggles())
        {
            ((RadioButton)(t)).setOnAction(new rbButtonHandler());
        }
        // set style for radio buttons
        for(Toggle t: tg.getToggles())
        {
            ((RadioButton)(t)).setStyle("-fx-font-size: 25;");
        }

        // boiler plate javafx code
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sorting Visualization");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Creates an ArrayList of myRectangle objects with the width = width/n
     * and a randomly generated height. This height will serve as the "value" of the
     * rectangle when the elements array is eventually sorted.
     *
     * @return myRectangle ArrayList
     */
    public static void createRectangles(ArrayList<Rectangle> elements,
                                        ArrayList<Rectangle> elements2) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) {
            // first create a randomly generated height between 0 and global height
            // variable
            int randomHeight = rand.nextInt(0, height);
            elements.add(new Rectangle());
            elements2.add(new Rectangle());
            // The Rectangle's height is their "value" in the array to be sorted
            elements.get(i).setHeight(randomHeight);
            elements2.get(i).setHeight(randomHeight);
            elements.get(i).setWidth(width / n);
            elements2.get(i).setWidth(width / n);
        }

    }

    /**
     * Takes the visualization MyArrayList (global listForVisualization) and colors it.
     * Also sets the height and style border.
     * @param elements
     * @param pane
     */
    public static void initRectangles(ArrayList<Rectangle> elements, Pane pane) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if(pane.getChildren().size() != 0)
        {
            pane.getChildren().clear();
        }
        q = null;
        q = new LinkedList<Moves>();
        for (int i = 0; i < n; i++) {
            pane.getChildren().add(elements.get(i));
            elements.get(i).setFill(getRandomColor());
            elements.get(i).setY(height - elements.get(i).getHeight());
            elements.get(i).setX(i * (width / n));
            elements.get(i).setStyle("-fx-stroke: BLACK; -fx-stroke-width: 3; ");
        }
    }

    /**
     * Returns a randomly generated Color that is neither white or black.
     */
    private static Color getRandomColor() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        int r = rand.nextInt(50, 215);
        int g = rand.nextInt(50, 215);
        int b = rand.nextInt(50, 215);
        return Color.rgb(r, g, b);
    }

    /**
     * BubbleSort method.
     *
     * @param elements ArrayList<myRectangle>
     */
    private static void bubbleSort(ArrayList<Rectangle> elements) {
        for (int j = 0; j < n - 1; j++) {
            for (int i = 0; i <= n - 2; i++) {
                if (elements.get(i).getHeight() > elements.get(i + 1).getHeight()) {
                    q.add(new Moves(i, i + 1));
                    swapRectangles(elements, i, i + 1);
                }
            }
        }
    }


    /**
     * Selection sort method
     * @param elements ArrayList<myRectangle>
     */
    private static void selectionSort(ArrayList<Rectangle> elements) {
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i; j < n; j++) {
                if (elements.get(j).getHeight() < elements.get(min).getHeight()) {
                    min = j;
                }
            }
            q.add(new Moves(min, i)); // store the swap into queue
            swapRectangles(elements, min, i);
            //std::swap(a[min], a[i]);  c++ line
        }
    }

    /**
     * Insertion sort method
     * @param elements ArrayList<myRectangle>
     */
    private static void insertionSort(ArrayList<Rectangle> elements) {
        int j;
        for (int i = 0; i < n; i++) {
            j = i;
            while (j > 0 && elements.get(j).getHeight() < elements.get(j - 1).getHeight()) {
                q.add(new Moves(j, j - 1)); // store the swap
                swapRectangles(elements, j, j - 1);
                j--;
            }
        }
    }

    /**
     * This function will take an array with lo and high indices. It will choose a
     * pivot element, in this case the very first element in the array, and partition
     * the array such that all elements less than or equal to the pivot are on the left
     * of the the pivot, and all elements greater than are on the right of the pivot.
     * It will also return the new index of the pivot after partitioning.According to
     * my calculations, which may be incorrect, the asymptotic complexity is O(n). When
     * used in a quick sort function, partition() is never called on arrays that
     * contain less than two elements in size. However, this partition will still work
     * correctly on arrays containing only one element.
     *
     *
     * I basically just copied this over from Algorithms class homework but modified
     * the code so it works with an ArrayList of myRectangle objects.
     *
     * @return int the new index of the pivot after partitioning
     */
    private static int partition(ArrayList<Rectangle> elements, int lo, int hi) {
        // taking the pivot to be the first element in the array
        int pivot = (int) elements.get(lo).getHeight();

        int i = lo; // left hand pointer starts at the index of the 1st element (pivot)
        int j = hi; // right hand pointer starts at the index of the last element

        while (true) {

            // increment left pointer until it reaches an element that is greater than
            // pivot or it reaches the hi of the array
            while (elements.get(i).getHeight() <= pivot && i < hi) {
                i++;
            }
            // decrement right pointer until reaches an element that is smaller than
            // pivot or it reaches the left most element (first element to right of pivot)
            while (elements.get(j).getHeight() > pivot && j > lo) {
                j--;
            }

            // if the pointer indices cross each other, or they are equal, swap the
            // pivot with a[j] and break out by returning new index of pivot
            if (j <= i) {
                q.add(new Moves(lo, j)); // add swap move to queue
                swapRectangles(elements, lo, j);
                return j;
            }
            // at this point, the pointers did not meet, arr[i] > pivot && arr[j] < pivot
            q.add(new Moves(i, j)); // add swap move to queue
            swapRectangles(elements, i, j);
        }

    }

    /**
     * Standard recursive implementation of QuickSort.
     *
     * int[] a[] - An arraylist<myRectangle>
     * int lo - the starting index of the Array
     * int hi - The ending index of the Array
     */
    private static void qsort(ArrayList<Rectangle> elements, int lo, int hi) {
        if (lo < hi) {
            int k = partition(elements, lo, hi);
            q.add(new Moves(lo, hi, k,true));
            qsort(elements, lo, k - 1);
            qsort(elements, k + 1, hi);
        }
    }

    /**
     * Only used during quicksort to help visualize the partitioning. The Rectangle
     * that each sub array is getting partitioned around will be colored white.
     * @param elements
     * @param lo
     * @param hi
     * @param k
     */
    private static void splitColorsAroundPartition(ArrayList<Rectangle> elements,
                                                   int lo, int hi, int k) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        Color c1 = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)).darker();
        Color c2 = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)).darker();
        for (int i = lo; i < k; i++) {
            elements.get(i).setFill(c1);
        }
        for (int j = k + 1; j <= hi; j++) {
            elements.get(j).setFill(c2);
        }
        elements.get(k).setFill(Color.WHITE);
    }

    /**
     * This method is used to swap rectangles in the visualization ArrayList (global
     * elements2). It will swap the position of the Rectangles specified in the indices
     * in the list.
     * @param rectangles
     * @param i
     * @param j
     */
    private static void swapRectangles(ArrayList<Rectangle> rectangles, int i, int j) {
        double temp = rectangles.get(i).getX();
        rectangles.get(i).setX(rectangles.get(j).getX());
        rectangles.get(j).setX(temp);
        Collections.swap(rectangles, i, j);
    }

    /**
     * Simply toggles the global Timeline object to play() or stop(), and modifies the
     * text of the Start/Resume button accordingly.
     */
    private static void toggle()
    {
        if(timeline.getStatus()== Animation.Status.RUNNING)
        {
            timeline.stop();
            button.setText("Resume");
        }
        else
        {
            timeline.play();
            button.setText("Pause");
        }
    }

    /**
     * This sets up the KeyFrame and Timeline objects to execute the code every t
     * milliseconds.
     * Each sorting algorithm looks best as a different rate, so the type of algorithm
     * used is specified in the boolean parameters.
     *
     * The times for Selection and Insertion are the same, and all the types of easily
     * changeable here.
     * @param type AlgoType
     */
    private static void setUpKeyFrame(AlgoType type)
    {
        KeyFrame kf;

        if(type == AlgoType.BUBBLE)
        {
            kf = new KeyFrame(Duration.millis(80), e -> {

                try {
                    if(q.size() == 0)
                    {
                        timeline.stop();
                        button.setDisable(true);
                    }
                    Moves moves = q.remove();
                    int i = moves.getI();
                    int j = moves.getJ();
                    swapRectangles(listForVisualizing, i, j);
                } catch (Exception ex) {
                }

            }

            );

        }
        else if(type == AlgoType.QUICKSORT)
        {
            kf = new KeyFrame(Duration.millis(250), e-> {
                try {
                    // Moves queue is empty, no more Moves to display.
                    if(q.size() == 0)
                    {
                        timeline.stop();
                        button.setDisable(true);
                        // Set all Rectangles to white, because they are all sorted.
                        for(Rectangle r: listForVisualizing) {
                            r.setFill(Color.WHITE);
                        }
                    }
                    Moves moves = q.remove();
                    if (moves.getAfterPartition()) {
                        splitColorsAroundPartition(listForVisualizing, moves.getLo(),
                                moves.getHi(), moves.getK());
                    } else {
                        int i = moves.getI();
                        int j = moves.getJ();
                        swapRectangles(listForVisualizing, i, j);
                    }
                } catch (Exception ex) {
                }
            }
            );

        }
        else {
            kf = new KeyFrame(Duration.millis(400), e -> {

                try {
                    if(q.size() == 0)
                    {
                        timeline.stop();
                        button.setDisable(true);
                    }
                    Moves moves = q.remove();
                    int i = moves.getI();
                    int j = moves.getJ();
                    swapRectangles(listForVisualizing, i, j);
                } catch (Exception ex) {

                }

            }
            );

        }
        timeline = null;
        timeline = new Timeline(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
