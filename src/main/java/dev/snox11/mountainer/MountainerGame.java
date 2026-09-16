package dev.snox11.mountainer;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.system.AppSettings;

/**
 * Initial bootstrap for Mountainer Climber.
 * This deliberately starts small: a working 3D window, camera, lighting,
 * and a placeholder mountain platform that we can replace with terrain.
 */
public final class MountainerGame extends SimpleApplication {

    public static void main(String[] args) {
        MountainerGame game = new MountainerGame();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Mountainer Climber");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        game.setSettings(settings);
        game.setShowSettings(false);
        game.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.55f, 0.72f, 0.88f, 1f));
        flyCam.setMoveSpeed(18f);
        cam.setLocation(new Vector3f(0f, 6f, 14f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        addLighting();
        addPrototypeGround();
    }

    private void addLighting() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.45f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.35f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(sun);
    }

    private void addPrototypeGround() {
        Box mesh = new Box(8f, 0.5f, 8f);
        Geometry ground = new Geometry("Prototype Ground", mesh);
        ground.setLocalTranslation(0f, -0.5f, 0f);

        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", new ColorRGBA(0.25f, 0.32f, 0.20f, 1f));
        material.setColor("Ambient", new ColorRGBA(0.18f, 0.22f, 0.15f, 1f));
        ground.setMaterial(material);

        rootNode.attachChild(ground);
    }
}
