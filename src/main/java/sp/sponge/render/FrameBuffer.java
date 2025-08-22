package sp.sponge.render;

import org.lwjgl.opengl.*;
import sp.sponge.render.shader.ShaderProgram;
import sp.sponge.render.shader.ShaderRegistry;

import java.nio.ByteBuffer;

public class FrameBuffer {
    private final int width;
    private final int height;
    private int frameBuffer;

    private int colorTexture;
    private int depthTexture;

    private final VertexBuffer screenBuffer;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.screenBuffer = new VertexBuffer(168, VertexBuffer.VertexDataType.POSITION_TEXTURE);
        this.init();
    }

    public void init() {
        this.frameBuffer = GL30.glGenFramebuffers();
        this.bind();
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        this.colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.colorTexture, 0);

        this.depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, this.width, this.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthTexture, 0);

        int i = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (i != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Error creating framebuffer");
        } else {
            System.out.println("OK");
        }

        unbind();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void bind() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBuffer);
    }

    public static void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void draw() {
        OpenGLSystem.disableDepthTest();
        // 0 1 2
        // 0 2 3
        this.screenBuffer.vertex(1.0f, 1.0f, 0.0f).texture(1.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, 1.0f, 0.0f).texture(0.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();

        this.screenBuffer.vertex(1.0f, 1.0f, 0.0f).texture(1.0f, 1.0f).next();
        this.screenBuffer.vertex(-1.0f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();
        this.screenBuffer.vertex(1.0f, -1.0f, 0.0f).texture(1.0f, 0.0f).next();

        ShaderRegistry.postShader.bind();
        this.screenBuffer.bind();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.colorTexture);
        ShaderRegistry.postShader.bindTexture("DiffuseTextureSampler", 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
        ShaderRegistry.postShader.bindTexture("DiffuseDepthSampler", 1);

        this.screenBuffer.drawElements();

        ShaderProgram.unbind();
        VertexBuffer.unbind();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

}
