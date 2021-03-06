/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmevr;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import jmevr.app.VRApplication;

/**
 *
 * @author reden
 */
public class TestOpenVR extends VRApplication {

    public static void main(String[] args){
        TestOpenVR test = new TestOpenVR();
        test.preconfigureVRApp(false, true, true);
        test.start();
    }
    
    Node boxes = new Node("");
    Spatial observer;
    boolean moveForward, moveBackwards, rotateLeft, rotateRight;
    Material mat;
    Node mainScene;
    
    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        initTestScene();
    }
    
    private void initTestScene(){
        this.flyCam.setMoveSpeed(10);
        mainScene = new Node("scene");
        observer = new Node("observer");
        mainScene.attachChild(SkyFactory.createSky(
                    assetManager, "Textures/Sky/Bright/spheremap.png", SkyFactory.EnvMapType.EquirectMap));
        
        Geometry box = new Geometry("", new Box(5,5,5));
        mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture noise = getAssetManager().loadTexture("Textures/noise.png");
        noise.setMagFilter(MagFilter.Nearest);
        noise.setMinFilter(MinFilter.Trilinear);
        noise.setAnisotropicFilter(16);
        mat.setTexture("ColorMap", noise);
        
        // gui element
        Picture test = new Picture("testpic");
        test.setImage(assetManager, "Textures/crosshair.png", true);
        test.setWidth(128f);
        test.setHeight(128f);
        test.setPosition(settings.getWidth() * 0.5f - 64f, settings.getHeight() * 0.5f - 64f);
        guiNode.attachChild(test);
        
        box.setMaterial(mat);
        
        Geometry box2 = box.clone();
        box2.move(15, 0, 0);
        box2.setMaterial(mat);
        Geometry box3 = box.clone();
        box3.move(-15, 0, 0);
        box3.setMaterial(mat);        
        
        boxes.attachChild(box);
        boxes.attachChild(box2);
        boxes.attachChild(box3);
        rootNode.attachChild(boxes);
        
        observer.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        
        VRApplication.setObserver(observer);
        mainScene.attachChild(observer);
        rootNode.attachChild(mainScene);
        
        generateAlignmentCoords();

        initInputs();
    }

     private void initInputs() {
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
         inputManager.addMapping("incShift", new KeyTrigger(KeyInput.KEY_Q));
         inputManager.addMapping("decShift", new KeyTrigger(KeyInput.KEY_E));
         inputManager.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
         inputManager.addMapping("back", new KeyTrigger(KeyInput.KEY_S));
         inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
         inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_D));
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if(name.equals("incShift") && keyPressed){
                    VRApplication.getVRGuiNode().adjustGuiDistance(-0.1f);
                }else if(name.equals("decShift") && keyPressed){
                    VRApplication.getVRGuiNode().adjustGuiDistance(0.1f);
                }
                if( name.equals("toggle") ) {
                    VRApplication.getVRGuiNode().positionGui();
                }
                if(name.equals("forward")){
                    if(keyPressed){
                        moveForward = true;
                    } else {
                        moveForward = false;
                    }
                } else if(name.equals("back")){
                    if(keyPressed){
                        moveBackwards = true;
                    } else {
                        moveBackwards = false;
                    }
                }else if(name.equals("left")){
                    if(keyPressed){
                        rotateLeft = true;
                    } else {
                        rotateLeft = false;
                    }
                } else if(name.equals("right")){
                    if(keyPressed){
                        rotateRight = true;
                    } else {
                        rotateRight = false;
                    }
                }
                
                
            }
        };
        inputManager.addListener(acl, "forward");
        inputManager.addListener(acl, "back");
        inputManager.addListener(acl, "left");
        inputManager.addListener(acl, "right");
        inputManager.addListener(acl, "toggle");
        inputManager.addListener(acl, "incShift");
        inputManager.addListener(acl, "decShift");
    }
     
     private float distance = 100f;
     private float prod = 0f;
     
     @Override
     public void simpleUpdate(float tpf){
         prod+=tpf;
         distance = 100f * FastMath.sin(prod);
         boxes.setLocalTranslation(0, 0, 200f+ distance);
         
         if(moveForward){
             observer.move(observer.getLocalRotation().getRotationColumn(2).mult(tpf*8f));
         }
         if(moveBackwards){
             observer.move(observer.getLocalRotation().getRotationColumn(2).mult(-tpf*8f));
         }
         if(rotateLeft){
             observer.rotate(0, 0.75f*tpf, 0);
         }
         if(rotateRight){
             observer.rotate(0, -0.75f*tpf, 0);
         }
     }
     
     private void generateAlignmentCoords() {
        float distance = 8;
        for (int x = 0; x < 35; x++) {
            float cos = FastMath.cos(x * FastMath.PI / 16f) * distance;
            float sin = FastMath.sin(x * FastMath.PI / 16f) * distance;
            Vector3f loc = new Vector3f(cos, 0, sin);
            addBox(loc);
            loc = new Vector3f(0, cos, sin);
            addBox(loc);
        }

    }

    private void addBox(Vector3f location) {
        Box b = new Box(0.3f, 0.3f, 0.3f);

        Geometry leftQuad = new Geometry("Box", b);
        leftQuad.rotate(0.5f, 0, 0);
        leftQuad.setMaterial(mat);
        leftQuad.setLocalTranslation(location);
        mainScene.attachChild(leftQuad);
    }
}
