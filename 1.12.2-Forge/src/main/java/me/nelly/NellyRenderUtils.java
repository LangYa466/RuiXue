package me.nelly;

import me.tiangong.CustomColor;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class NellyRenderUtils {

    public static int vao;
    public static int vbo;

    public static int color1Location;
    public static int color2Location;
    public static int program;


    public int createShader(String sourceCode, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, sourceCode);
        glCompileShader(shader);
        return shader;
    }
}
