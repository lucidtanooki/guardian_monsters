package main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.abilities;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.Node;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;
import de.limbusdev.utils.geometry.IntVec2;

/**
 * NodeWidget
 *
 * @author Georg Eckert 2017
 */

public class NodeWidget extends ImageButton implements Listener<Node> {

    private static ArrayMap<Node.Type,ArrayMap<Node.State,ImageButtonStyle>> nodeStyles;
    private AnimatedImage activationAnimation;

    private Node node;
    private IntVec2 offset;

    private Skin skin;

    private Node.State currentWidgetState;

    public NodeWidget(Skin skin, Node node) {

        super(skin,"board-" + node.type.toString().toLowerCase() + "-disabled");
        this.skin = skin;
        this.node = node;
        currentWidgetState = node.getState();

        switch(node.type) {
            case ABILITY:
            case METAMORPHOSIS:
            case EQUIPMENT: offset = new IntVec2(-16,-16); break;
            default: offset = new IntVec2(-8,-8); break;
        }

        // Animation
        Animation anim = new Animation(.12f,skin.getRegions("node-activation-animation"));
        activationAnimation = new AnimatedImage(anim);
        activationAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        changeStatus(node.getState(), true);

        // Add to Observable
        node.add(this);
    }

    private void playActivationAnimation() {
        if(offset != null) {
            activationAnimation.setPosition(-32 - offset.x, -32 - offset.y, Align.bottomLeft);
            this.addActor(activationAnimation);
        }
    }

    private void changeStatus(Node.State state, boolean initializing) {

        if(state == Node.State.ACTIVE && currentWidgetState != Node.State.ACTIVE && !initializing) {
            playActivationAnimation();
        }
        this.setStyle(getNodeStyles(skin).get(node.type).get(state));
        this.currentWidgetState = state;
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x+offset.x, y+offset.y, alignment);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x+offset.x, y+offset.y);
    }

    public Node getNode() {
        return node;
    }

    public static ArrayMap<Node.Type, ArrayMap<Node.State, ImageButtonStyle>> getNodeStyles(Skin skin) {

        if(nodeStyles == null) {
            setupNodeStyles(skin);
        }
        return nodeStyles;
    }

    private static void setupNodeStyles(Skin skin) {

        // Set Node ImageButton Styles
        nodeStyles = new ArrayMap<>();
        for(Node.Type t : Node.Type.values()) {
            nodeStyles.put(t,new ArrayMap<>());
            ArrayMap<Node.State, ImageButtonStyle> statusStyles = nodeStyles.get(t);

            for(Node.State s : Node.State.values()) {
                // Assembles Styles like    "board-" + "empty" +                    "-" + "disabled"
                String key = "board-" + t.toString().toLowerCase() + "-" + s.toString().toLowerCase();
                statusStyles.put(s,skin.get(key,ImageButtonStyle.class));
            }
        }
    }

    @Override
    public void receive(Signal<Node> signal, Node object) {

        changeStatus(object.getState(), false);
    }
}