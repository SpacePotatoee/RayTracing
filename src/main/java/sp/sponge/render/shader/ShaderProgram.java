package sp.sponge.render.shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import sp.sponge.Sponge;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

public class ShaderProgram {
    private final int vertexID;
    private final int fragmentID;
    private final int shaderProgram;

    private Matrix4f model = new Matrix4f();
    private Matrix4f view = new Matrix4f();
    private Matrix4f proj = new Matrix4f();

    private final FloatBuffer matrixArray;

    public ShaderProgram(String vertexShaderSrc, String fragmentShaderSrc) {
        this.matrixArray = BufferUtils.createFloatBuffer(16);

        Logger logger = Sponge.getInstance().getLogger();

        //Initialize vertex shader
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, vertexShaderSrc);
        GL20.glCompileShader(vertexID);

        int success = GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(vertexID, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling vertex shader");
            logger.info(GL20.glGetShaderInfoLog(vertexID, len));
        }

        //Initialize fragment shader
        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, fragmentShaderSrc);
        GL20.glCompileShader(fragmentID);

        success = GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(fragmentID, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling fragment shader");
            logger.info(GL20.glGetShaderInfoLog(fragmentID, len));
        }

        //link fragment and vertex shader
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexID);
        GL20.glAttachShader(shaderProgram, fragmentID);
        GL20.glLinkProgram(shaderProgram);

        success = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH);
            logger.severe("ERROR compiling shader proram");
            logger.info(GL20.glGetProgramInfoLog(shaderProgram, len));
        }
    }

    public void bind() {
        GL20C.glUseProgram(shaderProgram);
    }

    public static void unbind() {
        GL20C.glUseProgram(0);
    }

    public void setMatrices(Matrix4f model, Matrix4f view, Matrix4f proj) {
        this.model = new Matrix4f(model);
        this.view = new Matrix4f(view);
        this.proj = new Matrix4f(proj);
        this.uploadDefaultUniforms();
    }

    public void uploadDefaultUniforms() {
        int modelLocation = GL20.glGetUniformLocation(shaderProgram, "model");
//        int viewLocation = GL20.glGetUniformLocation(shaderProgram, "view");
        int projLocation = GL20.glGetUniformLocation(shaderProgram, "proj");

        float[] array = new float[16];

//        matrixArray.clear();
        this.proj.get(array);
        GL20.glUniformMatrix4fv(projLocation, false, array);

        this.model.mul(view).get(array);
        GL20.glUniformMatrix4fv(modelLocation, false, array);

//        this.view.get(array);
//        GL20.glUniformMatrix4fv(viewLocation, false, array);


    }

}
