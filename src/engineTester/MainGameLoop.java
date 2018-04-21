package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.Mesh;
import models.RawModel;
import models.TexturedModel;

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

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		
//		RawModel model = OBJLoader.loadObjModel("icsp", loader);
		RawModel model = OBJLoader.loadObjModel("cube", loader);
		
//		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("cloth")));
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("orange")));

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
        mesh.loadFromFile("hua2");
        RawModel meshModel = mesh.loadObjModel(loader);
        TexturedModel cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("snow2")));
        entities.add(new Entity(cubeModel, new Vector3f(0,0,0),0,0,0,3));
//		System.out.println("Faces: " + mesh.faces.size() + );

		while(!Display.isCloseRequested()){
			camera.move();
			
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
