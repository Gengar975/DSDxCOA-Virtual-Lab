package lab.ui;

import javafx.scene.Node;

/**
 * The visual part of a lesson. Each method may return null when the topic does not need that section.
 * <ul>
 *   <li>{@link #diagram()}      static or lightly animated drawing of the component</li>
 *   <li>{@link #table()}        truth table / function table / state table</li>
 *   <li>{@link #interactive()}  the "try it yourself" animated visualisation</li>
 * </ul>
 */
interface Lab {

    default Node diagram() { return null; }

    default Node table() { return null; }

    Node interactive();

    /** Stops timers / animations when the student leaves the page. */
    default void dispose() { }
}
