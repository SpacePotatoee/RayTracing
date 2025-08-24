package sp.sponge.util;

import sp.sponge.Sponge;
import sp.sponge.render.Mesh;
import sp.sponge.resources.ResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ObjParser {
    private static final HashMap<String, Mesh> cachedMeshes = new HashMap<>();

    public static Mesh objToMesh(String pathToObjFile) {
        String fullPath = "models/" + pathToObjFile + ".obj";

        if (cachedMeshes.containsKey(pathToObjFile)) {
            return cachedMeshes.get(pathToObjFile);
        }

        File objFile = ResourceManager.getAssetFile(fullPath);
        List<float[]> vertices = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<Mesh.Face> faces = new ArrayList<>();
        int totalFaces = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(objFile);
            String objectFileText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (String line : objectFileText.lines().toList()) {
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("vn")) {
                    String[] strings = getData(line);
                    if (strings.length < 3) {
                        throw new RuntimeException("Obj file has a vertex with less than 3 normal values");
                    }

                    float[] normalsArray = new float[] {
                            Float.parseFloat(strings[0]),
                            Float.parseFloat(strings[1]),
                            Float.parseFloat(strings[2])
                    };

                    normals.add(normalsArray);

                } else if (line.startsWith("vt")) {
                    
                } else if (line.startsWith("v")) {
                    String[] strings = getData(line);
                    if (strings.length < 3) {
                        throw new RuntimeException("Obj file has a vertex with less than 3 coordinates");
                    }
                    float[] vertexPositionsArray = new float[] {
                            Float.parseFloat(strings[0]),
                            Float.parseFloat(strings[1]),
                            Float.parseFloat(strings[2])
                    };

                    vertices.add(vertexPositionsArray);

                } else if (line.startsWith("f")) {
                    String[][] strings = getFaceData(line);

                    Mesh.Vertex[] face = new Mesh.Vertex[3];
                    for (int i = 0; i < 3; i++) {
                        float[] vertexPositions = vertices.get(parseInt(strings[i][0], vertices.size(), normals.size(), false) - 1);
                        float[] vertexNormals = normals.get(parseInt(strings[i][2], vertices.size(), normals.size(), true) - 1);

                        Mesh.Vertex vertex = new Mesh.Vertex(
                                vertexPositions[0],
                                vertexPositions[1],
                                vertexPositions[2],
                                vertexNormals[0],
                                vertexNormals[1],
                                vertexNormals[2]
                        );

                        face[i] = vertex;
                    }
                    faces.add(new Mesh.Face(face[0], face[1], face[2]));
                    totalFaces++;
                }
            }

            Mesh mesh = new Mesh(faces.size());

            for (Mesh.Face face : faces) {
                mesh.addFace(face);
            }
            cachedMeshes.put(pathToObjFile, mesh);
            Sponge.getInstance().getLogger().info("Generated " + totalFaces + " faces");
            return mesh;

        } catch (IOException e) {
            throw new RuntimeException("Error parsing obj file" + e);
        }

    }

    private static String[] getData(String line) {
        int numOfDataPoints = 0;
        int startingPoint = 0;
        for (char char1 : line.toCharArray()) {
            if (Character.isDigit(char1) || char1 == '-') {
                break;
            }
            startingPoint++;
        }

        for (char char1 : line.substring(startingPoint).toCharArray()) {
            if (char1 == ' ') {
                numOfDataPoints++;
            }
        }

        String[] strings = new String[numOfDataPoints + 1];

        int currentPosition = startingPoint, startPosition = startingPoint;
        int arrayPos = 0;
        while (currentPosition < line.length()) {
            for (char character : line.substring(currentPosition).toCharArray()) {
                if (character == ' ') {
                    break;
                }
                currentPosition++;
            }
            String data = line.substring(startPosition, currentPosition);
            strings[arrayPos++] = data;
            currentPosition++;
            startPosition = currentPosition;
        }

        return strings;
    }

    private static String[][] getFaceData(String line) {
        int startingPoint = 0;
        for (char char1 : line.toCharArray()) {
            if (Character.isDigit(char1) || char1 == '-') {
                break;
            }
            startingPoint++;
        }

        String[][] strings = new String[3][3];

        int currentPosition = startingPoint, startPosition = startingPoint;
        int vertexDataPos = -1;
        int arrayPos = 0;
        while (currentPosition < line.length()) {
            boolean shouldEndVertex = false;
            for (char character : line.substring(currentPosition).toCharArray()) {
                if (character == ' ' || character == '/') {
                    if (character == ' ') {
                        shouldEndVertex = true;
                    }
                    vertexDataPos++;
                    break;
                }

                currentPosition++;
            }

            if (currentPosition == line.length()) {
                vertexDataPos++;
            }

            String data = line.substring(startPosition, currentPosition);
            strings[arrayPos][vertexDataPos] = data;

            if (shouldEndVertex) {
                arrayPos++;
                vertexDataPos = -1;
            }
            currentPosition++;
            startPosition = currentPosition;
        }

        return strings;
    }

    private static int parseInt(String s, int numOfVertices, int numOfNormals, boolean normals) {
        if (Objects.equals(s, "") || s.isEmpty()) {
            return -1;
        }

        int integer = Integer.parseInt(s);

        //Negative indices
        if (integer < 0) {
            integer += (normals ? numOfNormals : numOfVertices) + 1;
        }

        return integer;
    }

    private record FaceData(VertexData v1, VertexData v2, VertexData v3){}

    private record VertexData(int vertex, int texCoord, int normal) {}

}
