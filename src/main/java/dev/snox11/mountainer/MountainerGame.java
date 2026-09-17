package dev.snox11.mountainer;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

import java.util.Random;

/** A small playable third-person 3D mountain-climbing prototype. */
public final class MountainerGame extends SimpleApplication implements ActionListener {
    private static final float WALK_SPEED = 8f;
    private static final float SPRINT_SPEED = 13f;
    private static final float GRAVITY = 25f;
    private static final float JUMP_SPEED = 10f;

    private final Node player = new Node("Climber");
    private final Random random = new Random(42);
    private boolean forward, backward, left, right, sprint;
    private float verticalVelocity;
    private float stamina = 100f;
    private BitmapText hud;

    public static void main(String[] args) {
        MountainerGame game = new MountainerGame();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Mountainer Climber - 3D Prototype");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        game.setSettings(settings);
        game.setShowSettings(false);
        game.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayFps(false);
        setDisplayStatView(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.48f, 0.70f, 0.90f, 1f));
        flyCam.setEnabled(false);

        addLighting();
        buildMountain();
        buildClimber();
        setupInput();
        setupHud();
        updateCamera();
    }

    private void addLighting() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.55f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.55f, -1f, -0.35f).normalizeLocal());
        sun.setColor(new ColorRGBA(1f, 0.96f, 0.86f, 1f).mult(1.25f));
        rootNode.addLight(sun);
    }

    private void buildMountain() {
        Geometry valley = box("Alpine Valley", 75f, 0.5f, 75f, new ColorRGBA(0.22f, 0.38f, 0.17f, 1f));
        valley.setLocalTranslation(0f, -0.5f, 0f);
        rootNode.attachChild(valley);

        // A stepped rocky route creates a real 3D climb without external art assets.
        for (int i = 0; i < 18; i++) {
            float y = i * 2.0f;
            float z = -i * 4.0f - 8f;
            float x = FastMath.sin(i * 0.85f) * 5f;
            float width = 7f - i * 0.13f;
            Geometry ledge = box("Rock Ledge " + i, width, 1.2f, 4.3f,
                    i > 13 ? new ColorRGBA(0.72f, 0.75f, 0.76f, 1f)
                            : new ColorRGBA(0.31f, 0.29f, 0.27f, 1f));
            ledge.setLocalTranslation(x, y, z);
            ledge.rotate(0f, FastMath.sin(i) * 0.12f, FastMath.sin(i * 1.7f) * 0.04f);
            rootNode.attachChild(ledge);
        }

        // Distant peaks make the scene feel like a mountain range.
        addPeak(-34f, -50f, 28f, 16f);
        addPeak(35f, -58f, 35f, 19f);
        addPeak(-50f, -72f, 42f, 23f);
        addPeak(54f, -82f, 46f, 25f);

        // Scatter rocks around base camp.
        for (int i = 0; i < 28; i++) {
            float x = random.nextFloat() * 65f - 32.5f;
            float z = random.nextFloat() * 45f - 12f;
            float size = 0.35f + random.nextFloat() * 1.2f;
            Geometry rock = new Geometry("Rock", new Sphere(8, 12, size));
            rock.setMaterial(lit(new ColorRGBA(0.28f, 0.27f, 0.25f, 1f)));
            rock.setLocalTranslation(x, size * 0.55f, z);
            rock.scale(1f, 0.65f + random.nextFloat() * 0.7f, 1f);
            rootNode.attachChild(rock);
        }

        // Summit marker.
        Geometry flagPole = new Geometry("Summit Pole", new Cylinder(8, 12, 0.08f, 4f));
        flagPole.setMaterial(lit(new ColorRGBA(0.18f, 0.18f, 0.18f, 1f)));
        flagPole.setLocalTranslation(FastMath.sin(17 * 0.85f) * 5f, 37f, -76f);
        rootNode.attachChild(flagPole);
    }

    private void addPeak(float x, float z, float height, float radius) {
        Cylinder peakMesh = new Cylinder(4, 12, radius, 0.7f, height, true, false);
        Geometry peak = new Geometry("Distant Peak", peakMesh);
        peak.setMaterial(lit(new ColorRGBA(0.34f, 0.36f, 0.37f, 1f)));
        peak.setLocalTranslation(x, height * 0.5f - 1f, z);
        rootNode.attachChild(peak);
    }

    private void buildClimber() {
        Geometry body = box("Jacket", 0.55f, 0.9f, 0.32f, new ColorRGBA(0.88f, 0.22f, 0.12f, 1f));
        body.setLocalTranslation(0f, 1.45f, 0f);
        player.attachChild(body);

        Geometry head = new Geometry("Head", new Sphere(12, 16, 0.42f));
        head.setMaterial(lit(new ColorRGBA(0.92f, 0.70f, 0.52f, 1f)));
        head.setLocalTranslation(0f, 2.65f, 0f);
        player.attachChild(head);

        Geometry backpack = box("Backpack", 0.48f, 0.65f, 0.28f, new ColorRGBA(0.10f, 0.16f, 0.18f, 1f));
        backpack.setLocalTranslation(0f, 1.55f, 0.48f);
        player.attachChild(backpack);

        Geometry leftLeg = box("Left Leg", 0.20f, 0.55f, 0.20f, new ColorRGBA(0.12f, 0.13f, 0.15f, 1f));
        leftLeg.setLocalTranslation(-0.28f, 0.45f, 0f);
        player.attachChild(leftLeg);
        Geometry rightLeg = leftLeg.clone(false);
        rightLeg.setName("Right Leg");
        rightLeg.setLocalTranslation(0.28f, 0.45f, 0f);
        player.attachChild(rightLeg);

        player.setLocalTranslation(0f, 0f, 8f);
        rootNode.attachChild(player);
    }

    private void setupInput() {
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Sprint", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Forward", "Backward", "Left", "Right", "Sprint", "Jump");
    }

    private void setupHud() {
        hud = new BitmapText(guiFont);
        hud.setSize(20f);
        hud.setColor(ColorRGBA.White);
        hud.setLocalTranslation(24f, cam.getHeight() - 28f, 0f);
        guiNode.attachChild(hud);
    }

    @Override
    public void onAction(String name, boolean pressed, float tpf) {
        switch (name) {
            case "Forward" -> forward = pressed;
            case "Backward" -> backward = pressed;
            case "Left" -> left = pressed;
            case "Right" -> right = pressed;
            case "Sprint" -> sprint = pressed;
            case "Jump" -> {
                if (pressed && player.getLocalTranslation().y <= terrainHeight(player.getLocalTranslation().x, player.getLocalTranslation().z) + 0.05f) {
                    verticalVelocity = JUMP_SPEED;
                }
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f move = new Vector3f();
        if (forward) move.z -= 1f;
        if (backward) move.z += 1f;
        if (left) move.x -= 1f;
        if (right) move.x += 1f;

        boolean moving = move.lengthSquared() > 0f;
        float speed = WALK_SPEED;
        if (sprint && moving && stamina > 0f) {
            speed = SPRINT_SPEED;
            stamina = Math.max(0f, stamina - 20f * tpf);
        } else {
            stamina = Math.min(100f, stamina + 12f * tpf);
        }

        if (moving) {
            move.normalizeLocal().multLocal(speed * tpf);
            player.move(move);
            player.lookAt(player.getLocalTranslation().add(move), Vector3f.UNIT_Y);
        }

        Vector3f pos = player.getLocalTranslation();
        float ground = terrainHeight(pos.x, pos.z);
        verticalVelocity -= GRAVITY * tpf;
        pos.y += verticalVelocity * tpf;
        if (pos.y < ground) {
            pos.y = ground;
            verticalVelocity = 0f;
        }
        player.setLocalTranslation(pos);

        updateCamera();
        hud.setText(String.format("MOUNTAINER CLIMBER   Altitude: %.0f m   Stamina: %.0f%%\nWASD move   Shift sprint   Space jump   Reach the summit!", pos.y * 25f, stamina));
    }

    // Height approximation matching the staircase route; enough for this first playable prototype.
    private float terrainHeight(float x, float z) {
        if (z > -6f) return 0f;
        int step = Math.min(17, Math.max(0, (int) ((-z - 6f) / 4f)));
        float centerX = FastMath.sin(step * 0.85f) * 5f;
        float width = 7f - step * 0.13f;
        float centerZ = -step * 4f - 8f;
        if (Math.abs(x - centerX) <= width && Math.abs(z - centerZ) <= 4.5f) {
            return step * 2f + 1.2f;
        }
        return 0f;
    }

    private void updateCamera() {
        Vector3f target = player.getLocalTranslation().add(0f, 1.7f, 0f);
        Vector3f desired = target.add(0f, 5.5f, 11f);
        cam.setLocation(cam.getLocation().interpolateLocal(desired, 0.09f));
        cam.lookAt(target, Vector3f.UNIT_Y);
    }

    private Geometry box(String name, float x, float y, float z, ColorRGBA color) {
        Geometry geometry = new Geometry(name, new Box(x, y, z));
        geometry.setMaterial(lit(color));
        return geometry;
    }

    private Material lit(ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", color);
        material.setColor("Ambient", color.mult(0.55f));
        material.setColor("Specular", ColorRGBA.White.mult(0.08f));
        material.setFloat("Shininess", 8f);
        return material;
    }
}
