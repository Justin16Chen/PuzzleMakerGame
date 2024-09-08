package gameplay.MapLoading;

import java.util.Scanner;
import java.io.*;

import java.util.*;

import gameplay.gameObjects.*;

public class GameLoader {

    final private static String ROOT_PATH = "res/levels";

    public static MapInfo getMapInfo(String fileName) {
        File file = new File(ROOT_PATH + "/" + fileName);

        int mapWidth = 0;
        int mapHeight = 0;
        ArrayList<GameObjectPrimitive> gameObjectPrimitives = new ArrayList<GameObjectPrimitive>();

        try {
            Scanner scanner = new Scanner(file);

            // read in the map dimensions
            try {
                scanner.next();
                mapWidth = scanner.nextInt();
                scanner.next();
                mapHeight = scanner.nextInt();

                // skip game objects header
                scanner.nextLine();
                scanner.nextLine();

                while (scanner.hasNextLine()) {
                    // read in the game objects
                    String name;
                    int positionx, positiony;
                    HashMap<String, String> info = new HashMap<String, String>();
    
                    String line = scanner.nextLine();     // skip opening braces
                    if (!line.equals("{")) {
                        break;
                    }
                    scanner.next();         // skip name identifier
                    name = scanner.next();
                    scanner.nextLine();     // new line
                    scanner.next();         // skip position identifier
                    positionx = scanner.nextInt();
                    positiony = scanner.nextInt();

                    // not all objects will have object info
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();     // new line
                        scanner.next();         // skip info header
                        String identifier = scanner.next();
                        while (scanner.hasNextLine() && !identifier.equals("}")) {
                            info.put(identifier.substring(0, identifier.length() - 1), scanner.next());
                            identifier = scanner.next();
                        }
                    }
                
                    // create game object primitive
                    gameObjectPrimitives.add(new GameObjectPrimitive(name, positionx, positiony, info));
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: " + fileName + " is not formatted correctly");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new MapInfo(mapWidth, mapHeight, gameObjectPrimitives);
    }

    public static void main(String[] args) {
        System.out.println(getMapInfo("0.txt"));
    }
}
