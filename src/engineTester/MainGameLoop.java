package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.Mesh;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MainGameLoop {

	public static void main(String[] args) throws InterruptedException {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		
//		RawModel model = OBJLoader.loadObjModel("icsp", loader);
		RawModel model = OBJLoader.loadObjModel("cube", loader);
		
//		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("cloth")));
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("snow2")));

        List<Entity> entities = new ArrayList<Entity>();
//		entities.add(new Entity(staticModel, new Vector3f(5,5,5),0,0,0,3));
//        entities.add(new Entity(staticModel, new Vector3f(5,0,0),0,0,0,3));

//        Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
//        Light light = new Light(new Vector3f(0,1000,0),new Vector3f(1,1,1));
        Light light = new Light(new Vector3f(1000,1000,1000),new Vector3f(1,1,1));

//		Terrain terrain = new Terrain(0,0,loader,new ModelTexture(loader.loadTexture("grass")));
//		Terrain terrain2 = new Terrain(1,0,loader,new ModelTexture(loader.loadTexture("grass")));

        Camera camera = new Camera();
        MasterRenderer renderer = new MasterRenderer();

        Mesh mesh = new Mesh();
        mesh.loadFromFile("cube");
//		mesh.loadFromFile("hollow_cube");
//		mesh.loadFromFile("spaceship2");
//		Mesh.subdivide(mesh);

		// Creases
//		mesh.edges.get(0).crease = true;
//		mesh.edges.get(0).pair.crease = true;
//		mesh.edges.get(1).crease = true;
//		mesh.edges.get(1).pair.crease = true;
//		mesh.edges.get(2).crease = true;
//		mesh.edges.get(2).pair.crease = true;

		int crease_list[] = {1,4,9,14};
		for(int i:crease_list)
		{
			mesh.edges.get(i).crease = true;
			mesh.edges.get(i).pair.crease = true;
		}

		RawModel meshModel = mesh.loadObjModel(loader);
        TexturedModel cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("snow1")));
        entities.add(new Entity(cubeModel, new Vector3f(0,0,0),0,0,0,3));
//		System.out.println("Faces: " + mesh.faces.size() + );

		while(!Display.isCloseRequested()){
			camera.move();

			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				Mesh.subdivide(mesh);
				meshModel = mesh.loadObjModel(loader);
				cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("snow1")));
				entities.clear();
				entities.add(new Entity(cubeModel, new Vector3f(0,0,0),0,0,0,3));
				System.out.println("Faces: " + mesh.faces.size() + ", Vertices: " + mesh.vertices.size() + ", Edges: " + mesh.edges.size());
				Thread.sleep(1000);
			}

//			renderer.processTerrain(terrain);
//			renderer.processTerrain(terrain2);


			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
