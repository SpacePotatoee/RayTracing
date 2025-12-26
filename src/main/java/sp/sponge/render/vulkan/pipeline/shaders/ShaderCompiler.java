package sp.sponge.render.vulkan.pipeline.shaders;

import org.lwjgl.util.shaderc.Shaderc;
import sp.sponge.Sponge;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.logging.Logger;

public class ShaderCompiler {

    public static byte[] compileShader(String shaderCode, int shaderType) {
        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();
            Shaderc.shaderc_compile_options_set_target_spirv(options, Shaderc.shaderc_spirv_version_1_4);

            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    shaderCode,
                    shaderType,
                    "shader.glsl",
                    "main",
                    options
            );

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Failed to compile shader: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }

    public static void compiledShaderIfChanged(String unCompiledShaderFile, int shaderType) {
        byte[] compiledShader;
        Logger logger = Sponge.getInstance().getLogger();
        try {
            File originalFile = Sponge.getAssetFile(unCompiledShaderFile);
            File compiledFile = Sponge.getAssetFile(unCompiledShaderFile + ".spv");
            if (!compiledFile.exists() || originalFile.lastModified() > compiledFile.lastModified()) {
                logger.info("Compiling " + originalFile.getPath() + " to " + compiledFile.getPath());

                String shaderCode = new String(Files.readAllBytes(originalFile.toPath()));
                compiledShader = compileShader(shaderCode, shaderType);
                Files.write(compiledFile.toPath(), compiledShader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
