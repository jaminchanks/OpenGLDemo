package com.rhythm7.opengldemo

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.rhythm7.opengldemo.Constants.FLOAT_SIZE
import com.rhythm7.opengldemo.Shader.POSITION_COORD_COUNT
import com.rhythm7.opengldemo.Shader.TEXTURE_COORD_COUNT
import com.rhythm7.opengldemo.Shader.VERTEX_COMPONENT_COUNT
import com.rhythm7.opengldemo.Shader.a_Position
import com.rhythm7.opengldemo.Shader.a_TextureCoordinates
import com.rhythm7.opengldemo.Shader.fragmentShaderCode
import com.rhythm7.opengldemo.Shader.u_Matrix
import com.rhythm7.opengldemo.Shader.u_TextureUnit
import com.rhythm7.opengldemo.Shader.vertexShaderCode
import com.rhythm7.opengldemo.Shader.verticesData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Jaminchanks on 2018-01-21.
 */
 class TextureRender(private var context: Context) : GLSurfaceView.Renderer {

    private var uMatrixLocation: Int = 0
    private var projectionMatrix = FloatArray(16)

    var mProgramId: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        initProgram()
        initialize()
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        matrixMap(width, height)
    }


    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }


    private fun initProgram() {
        val vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgramId = createProgram(vertexShader, fragmentShader)
        glUseProgram(mProgramId)
    }


    private fun initialize() {
        uMatrixLocation = glGetUniformLocation(mProgramId, u_Matrix)

        verticesData
                .toFloatBuffer()
                ?.apply {
                    setVertexAttribPointer(mProgramId, a_Position, POSITION_COORD_COUNT, 0,
                            VERTEX_COMPONENT_COUNT * FLOAT_SIZE)
                    setVertexAttribPointer(mProgramId, a_TextureCoordinates, TEXTURE_COORD_COUNT, POSITION_COORD_COUNT,
                            VERTEX_COMPONENT_COUNT * FLOAT_SIZE)
                }

        glGetUniformLocation(mProgramId, u_TextureUnit)
                .let {
                    context.createBitmap(R.drawable.avatar)?.load2DTexture(it)
                }
    }


    /**
     * 矩阵变换投影
     */
    private fun matrixMap(width: Int, height: Int) {
        val aspectRatio = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }

        //平行投影
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f,1f, 0f, 10f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 0f, 10f)
        }

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)
    }


}
