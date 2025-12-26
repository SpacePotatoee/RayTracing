package sp.sponge.util;

import sp.sponge.Sponge;
import sp.sponge.render.vulkan.model.Mesh;
import sp.sponge.util.math.Vec3f;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ObjParser {
    private static final HashMap<String, Mesh> cachedMeshes = new HashMap<>();

    public static Mesh objToMesh(String pathToObjFile) {
        String fullPath = "models/" + pathToObjFile + ".obj";

        if (cachedMeshes.containsKey(pathToObjFile)) {
            return cachedMeshes.get(pathToObjFile);
        }

        File objFile = Sponge.getAssetFile(fullPath);
        List<Vec3f> vertices = new ArrayList<>();
        List<Vec3f> normals = new ArrayList<>();
        List<Mesh.Face> faces = new ArrayList<>();
        int totalFaces = 0;
        try (FileInputStream fileInputStream = new FileInputStream(objFile)) {
            String objectFileText = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (String line : objectFileText.lines().toList()) {
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] piecez = line.split(" ");
                List<String> pieces = new ArrayList<>(Arrays.asList(piecez));
                //Double spaces
                pieces.removeIf(String::isEmpty);

                switch (pieces.getFirst()) {
                    case "v" -> {
                        if (pieces.size() > 4) {
                            throw new RuntimeException("Error parsing Obj File " + pathToObjFile + " " + line);
                        }
                        Vec3f vertexPositionsArray = new Vec3f(
                                Float.parseFloat(pieces.get(1)),
                                Float.parseFloat(pieces.get(2)),
                                Float.parseFloat(pieces.get(3))
                        );

                        vertices.add(vertexPositionsArray);
                    }
                    case "vn" -> {
                        if (pieces.size() > 4) {
                            throw new RuntimeException("Error parsing Obj File " + pathToObjFile);
                        }

                        Vec3f normalsArray = new Vec3f (
                                Float.parseFloat(pieces.get(1)),
                                Float.parseFloat(pieces.get(2)),
                                Float.parseFloat(pieces.get(3))
                        );

                        normals.add(normalsArray);
                    }
                    case "f" -> {
                        int numOfVertices = pieces.size() - 1;
                        if (numOfVertices < 3 || numOfVertices > 4) {
                            //Not triangle or quad
                            throw new RuntimeException("Obj parser does not support nonagons");
                        }

                        int[] positionIndex = new int[numOfVertices];
                        int[] normalIndex = new int[numOfVertices];
                        for (int i = 1; i < pieces.size(); i++) {
                            String[] vertexData = pieces.get(i).split("/");
                            if (vertexData.length == 0) continue;

                            if (vertexData.length != 3) {
                                throw new RuntimeException("Not enough vertex data in " + pathToObjFile);
                            }

                            positionIndex[i - 1] = Integer.parseInt(vertexData[0]) - 1;
                            normalIndex[i - 1] = Integer.parseInt(vertexData[2]) - 1;
                        }

                        //Build Vertices
                        Mesh.Vertex[] faceVertices = new Mesh.Vertex[numOfVertices];
                        for (int j = 0; j < numOfVertices; j++) {
                            Vec3f vertexPos = vertices.get(positionIndex[j]);
                            Vec3f vertexNormal = normals.get(normalIndex[j]);
                            faceVertices[j] = new Mesh.Vertex(
                                    vertexPos.x,
                                    vertexPos.y,
                                    vertexPos.z,
                                    vertexNormal.x,
                                    vertexNormal.y,
                                    vertexNormal.z
                            );
                        }

                        faces.add(new Mesh.Face(faceVertices[0], faceVertices[1], faceVertices[2]));

                        if (numOfVertices == 4) {
                            faces.add(new Mesh.Face(faceVertices[0], faceVertices[2], faceVertices[3]));
                        }
                        totalFaces++;
                    }
                }
            }

            Mesh mesh = new Mesh(faces.size());

            for (Mesh.Face face : faces) {
                mesh.addFace(face);
            }
            cachedMeshes.put(pathToObjFile, mesh);
            Sponge.getInstance().getLogger().info("Generated " + totalFaces + " triangles");
            return mesh;

        } catch (IOException e) {
            throw new RuntimeException("Error parsing obj file" + e);
        }
    }
}
