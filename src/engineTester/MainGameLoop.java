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

	public static void addRandom(ArrayList<Entity> entities, RawModel[] meshModels, Loader loader, Camera camera){
		Random random = new Random();

		int x = 100;
		int y = 100;
		int z = 200;
//		camera.z = z;
		float maxDist = Vector3f.sub(new Vector3f(-x,-y,-z),camera.getPosition(),null).lengthSquared();
		maxDist = (float) Math.sqrt(maxDist);
		float minDist = camera.getPosition().z - z;
		minDist =  Math.abs(minDist);
		System.out.println(minDist + " " + maxDist);

		for(int i=0;i<5;i++){
			Vector3f position = new Vector3f(random.nextFloat()*2*x - x,random.nextFloat()*2*y - y,random.nextFloat()*2*z - z);
			float distance = Vector3f.sub(position,camera.getPosition(),null).lengthSquared();
			distance = (float) Math.sqrt(distance);
			int num = (int) Math.floor((distance - minDist) * 5.0 / (maxDist - minDist));

			System.out.println(position + ": " + distance + " " + num);
			TexturedModel cubeModel = new TexturedModel(meshModels[num], new ModelTexture(loader.loadTexture("checker_b")));
			Entity ent = new Entity(cubeModel, position,0,0,0,5);
			entities.add(ent);
		}
	}

	public static void main(String[] args) throws InterruptedException {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

//		RawModel model = OBJLoader.loadObjModel("icsp", loader);
		RawModel model = OBJLoader.loadObjModel("cube", loader);

//		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("cloth")));
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("snow2")));

        ArrayList<Entity> entities = new ArrayList<Entity>();
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

//		int crease_list[] = {1,4,9,14};
//		for(int i:crease_list)
//		{
//			mesh.edges.get(i).crease = true;
//			mesh.edges.get(i).pair.crease = true;
//		}

		RawModel meshModel = mesh.loadObjModel(loader);
        TexturedModel cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("orange")));
//
		boolean staticDistance = false;
		boolean dynamicDistance = false;
		boolean right = true;

//		mesh.subdivide(4);
//		meshModel = mesh.loadObjModel(loader);
//		cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("snow1")));
		entities.add(new Entity(cubeModel, new Vector3f(0,0,0),0,0,0,2));


//		Random random = new Random();
//		RawModel meshModels[] = new RawModel[5];
//		TexturedModel cubeModels[] = new TexturedModel[5];
//
//
//		int x = 100;
//		int y = 100;
//		int z = 200;
//		for(int i = 0; i < 5; i++) {
//			meshModels[4 - i] = mesh.loadObjModel(loader);
//			cubeModels[4 - i] = new TexturedModel(meshModels[4-i], new ModelTexture(loader.loadTexture("snow1")));
//			Mesh.subdivide(mesh);
//		}
//
//		float maxDist = Vector3f.sub(new Vector3f(-x,-y,-z),camera.getPosition(),null).lengthSquared();
//
//		maxDist = (float) Math.sqrt(maxDist);
//		float minDist = camera.getPosition().z - z;
//		minDist =  Math.abs(minDist);
//		System.out.println(minDist + " " + maxDist);
//
//		Vector3f positions[] = new Vector3f[5];
//		for (int i = 0; i < 5; i++) {
//			positions[i] = new Vector3f(random.nextFloat() * 2 * x - x, random.nextFloat() * 2 * y - y, random.nextFloat() * 2 * z - z);
//		}
//
//		if(staticDistance){
//			addRandom(entities,meshModels, loader,camera);
//		}

		int entNum=5;
		while(!Display.isCloseRequested()){
			camera.move();
//			System.out.println(camera.getPosition());
			if(right) {
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					Mesh.subdivide(mesh);
					meshModel = mesh.loadObjModel(loader);
					cubeModel = new TexturedModel(meshModel, new ModelTexture(loader.loadTexture("orange")));
//					entities.clear();
//					entities.add(new Entity(cubeModel, new Vector3f(0, 0, 0), 0, 0, 0, 2));
					entities.add(new Entity(cubeModel, new Vector3f(entNum, 0, 0), 0, 0, 0, 2));
					entNum += 5;
					System.out.println("Faces: " + mesh.faces.size() + ", Vertices: " + mesh.vertices.size() + ", Edges: " + mesh.edges.size());
					Thread.sleep(1000);
				}
			}

//			renderer.processTerrain(terrain);
//			renderer.processTerrain(terrain2);
//			if(dynamicDistance) {
//				entities.clear();
//				for (int i = 0; i < 5; i++) {
//					Vector3f position = positions[i];
//					float distance = Vector3f.sub(position, camera.getPosition(), null).lengthSquared();
//					distance = (float) Math.sqrt(distance);
//					int num = (int) Math.floor((distance - minDist) * 5.0 / (maxDist - minDist));
//
//					if(num<0) {
//						num = 0;
//					}
//					if(num>4) {
//						num = 4;
//					}
////					System.out.println(position + ": " + distance + " " + num);
//					Entity ent = new Entity(cubeModels[num], position, 0, 0, 0, 15);
//					entities.add(ent);
//				}
//			}
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
