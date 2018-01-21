package com.rhythm7.opengldemo

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log
import android.widget.Toast
import com.rhythm7.opengldemo.Constants.FLOAT_SIZE
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.opengl.GLES20.glEnableVertexAttribArray


//扩展函数

/**
 * 判断设备是否支持OpenGL ES2
 */
fun Context.isSupportES2(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val configurationInfo = activityManager.deviceConfigurationInfo
    return configurationInfo.reqGlEsVersion >= 0x20000
}


fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


/**
 * 加载纹理
 */
fun Bitmap.load2DTexture(textureUnitId: Int) {
    val tag = "loadTexture()"

    val textureIds = IntArray(1)
    glGenTextures(1, textureIds, 0)

    if (textureIds[0] != 0) {
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureIds[0])

        //设置过滤器，如果不设置，纹理将无法正常显示
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        //加载图片到纹理
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, this, 0)

        // 注意，以下代码可能报错: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0).
        // 如果发生这种错误，请将图片压缩成正方形，最后的显示效果不会有影响，因为有纹理坐标的存在
        glGenerateMipmap(GL_TEXTURE_2D)

        recycle()

        //解绑纹理
        glBindTexture(GL_TEXTURE_2D, 0)

        //先激活纹理单元0
        glActiveTexture(GL_TEXTURE0)
        //绑定纹理到纹理单元0
        glBindTexture(GL_TEXTURE_2D, textureIds[0])
        //读取纹理单元0以获取纹理数据
        glUniform1i(textureUnitId, 0)
        return
    }

    Log.e(tag, "Could not generate a new OpenGL texture object.")
}




/**
 * float数组转化为floatBuffer
 */
fun FloatArray.toFloatBuffer() = ByteBuffer
        .allocateDirect(size * FLOAT_SIZE)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        ?.put(this)


/**
 * 设置顶点数据到program中
 * @param programId
 * @param name 顶点在GLSL中的变量名
 * @param componentCount 每个顶点的组成
 * @param dataOffset 读取数据的起始位置
 * @param stride 每读取一个顶点跳过的字节数
 */
fun FloatBuffer.setVertexAttribPointer(programId: Int, name: String,
                                       componentCount: Int, dataOffset: Int = 0, stride: Int = 0) {
    position(dataOffset)
    glGetAttribLocation(programId, name).let {
        glVertexAttribPointer(it, componentCount, GL_FLOAT,
                false, stride, this)
        glEnableVertexAttribArray(it)
    }

    position(0)
}

/**
 * 创建一个bitmap对象
 */
fun Context.createBitmap(resourceId: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inScaled = false
    val bitmap =  BitmapFactory.decodeResource(resources, resourceId, options)

    if (bitmap == null) {
        Log.e("loadTexture", "Resource ID $resourceId could not be decoded")
    }

    return bitmap
}


/**
 * 链接vertex shader和fragment shader到program中
 * 如果成功，返回一个program对象Id，否则返回0
 */
fun createProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
    val tag = "createProgram()"

    //创建一个program对象
    val programObjectId = glCreateProgram()

    if (programObjectId != 0) {
        //将shader 加载到program上，并链接program
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        glLinkProgram(programObjectId)

        //获取上述操作的结果
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
        Logger.i(tag, "Result of linking program\n:" +
                " ${glGetShaderInfoLog(programObjectId)}")

        //处理链接结果
        if (linkStatus[0] != 0) {
            return programObjectId
        }

        glDeleteProgram(programObjectId)
        Logger.w(tag, "Linking program failed")
        return 0
    }

    Logger.e(tag, "Could not create new program")
    return 0
}

/**
 * 编译shader, 如果成功，返回shader的对象Id，否则返回0
 */
fun createShader(shaderType: Int, shaderCode: String): Int {
    val tag = "createShader()"

    //创建一个shader对象
    val shaderObjectId = glCreateShader(shaderType)
    if (shaderObjectId != 0) {
        //加载shader代码
        glShaderSource(shaderObjectId, shaderCode)
        //编译shader代码
        glCompileShader(shaderObjectId)

        //获取编译结果
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
        Logger.i(tag, "Result of compile source $shaderCode \n:" +" ${glGetShaderInfoLog(shaderObjectId)}")
        //判断编译结果
        if (compileStatus[0] != 0) {
            return shaderObjectId //编译成功
        }

        glDeleteShader(shaderObjectId)
        Logger.e(tag, "Compilation of shader failed.")
        return 0
    }

    Logger.e(tag, "Could not create new shader.")
    return 0
}