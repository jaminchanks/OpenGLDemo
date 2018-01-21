package com.rhythm7.opengldemo

/**
 * Created by Jaminchanks on 2018-01-21.
 */
object Shader {
    val u_Matrix = "u_Matrix"
    val a_Position = "a_Position"
    val a_TextureCoordinates = "a_TextureCoordinates"
    val u_TextureUnit = "u_TextureUnit"

    val VERTEX_COMPONENT_COUNT = 4
    val POSITION_COORD_COUNT = 2
    val TEXTURE_COORD_COUNT = 2

    val verticesData = floatArrayOf(
            //X, Y, S, T
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.5f,   0f, 1f,
            0.5f, -0.5f,   1f, 1f,
            0.5f,  0.5f,   1f, 0f,
            -0.5f,  0.5f,   0f, 0f,
            -0.5f, -0.5f,   0f, 1f
    )

    val vertexShaderCode =
            """
        uniform mat4 u_Matrix;
        attribute vec4 a_Position;
        attribute vec2 a_TextureCoordinates;
        varying vec2 v_TextureCoordinates;
        void main()
        {
            v_TextureCoordinates = a_TextureCoordinates;
            gl_Position = u_Matrix * a_Position;
        }
        """

    val fragmentShaderCode =
            """
        precision mediump float;
        uniform sampler2D u_TextureUnit;
        varying vec2 v_TextureCoordinates;
        varying mat4 v_Matrix;
        void main()
        {
            gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
        }
        """

}