package com.siiri.record.audio

import com.cleveroad.audiovisualization.DbmHandler

/**
 * @author: dinglei
 * @date: 2020/10/12 13:44
 */
class VisualizerHandler : DbmHandler<Float>() {

    override fun onDataReceivedImpl(
        paramAmplitude: Float,
        layersCount: Int,
        dBmArray: FloatArray,
        ampsArray: FloatArray
    ) {
        var amplitude = paramAmplitude
        amplitude /= 100
        if (amplitude <= 0.5) {
            amplitude = 0.0f
        } else if (amplitude > 0.5 && amplitude <= 0.6) {
            amplitude = 0.2f
        } else if (amplitude > 0.6 && amplitude <= 0.7) {
            amplitude = 0.6f
        } else if (amplitude > 0.7) {
            amplitude = 1f
        }
        try {
            dBmArray[0] = amplitude
            ampsArray[0] = amplitude
        } catch (e: Exception) {
        }
    }

    fun stop() {
        try {
            calmDownAndStopRendering()
        } catch (e: java.lang.Exception) {
        }
    }

}