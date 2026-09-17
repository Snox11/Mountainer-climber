# Mountainer Climber

A 3D mountain-climbing game written in Java with jMonkeyEngine.

## Current prototype

The first milestone provides:

- Java 21 + Gradle project structure
- jMonkeyEngine 3D rendering
- LWJGL desktop backend
- Bullet physics dependency ready for gameplay
- 1280x720 game window with VSync
- Basic camera and lighting
- Placeholder ground for validating the scene

## Run locally

Requirements:

- JDK 21
- Gradle installed locally (a Gradle wrapper will be added next)

Run:

```bash
gradle run
```

## Planned architecture

The game will be developed in vertical slices rather than building every system at once:

1. Player controller and third-person camera
2. Procedural/heightmap mountain terrain and collision
3. Climbing system: grip points, stamina, slipping, ledges, ropes
4. Traversal: walking, scrambling, jumping and falling
5. Weather, altitude, temperature and survival pressure
6. Checkpoints/base camps and summit progression
7. UI, audio, particles, post-processing and game feel
8. Art pass, optimization and packaging

## Development branch

Initial project setup is being built on `feat/initial-3d-game`.
