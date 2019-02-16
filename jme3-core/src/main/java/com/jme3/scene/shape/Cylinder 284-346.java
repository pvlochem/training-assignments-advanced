/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// $Id: Cylinder.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme3.scene.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import static com.jme3.util.BufferUtils.*;
import java.io.IOException;
import java.nio.FloatBuffer;


        // generate the cylinder itself
        Vector3f tempNormal = new Vector3f();
        for (int axisCount = 0, i = 0; axisCount < axisSamples; axisCount++, i++) {
            float axisFraction;
            float axisFractionTexture;
            int topBottom = 0;
            if (!closed) {
                axisFraction = axisCount * inverseAxisLess; // in [0,1]
                axisFractionTexture = axisFraction;
            } else {
                if (axisCount == 0) {
                    topBottom = -1; // bottom
                    axisFraction = 0;
                    axisFractionTexture = inverseAxisLessTexture;
                } else if (axisCount == axisSamples - 1) {
                    topBottom = 1; // top
                    axisFraction = 1;
                    axisFractionTexture = 1 - inverseAxisLessTexture;
                } else {
                    axisFraction = (axisCount - 1) * inverseAxisLess;
                    axisFractionTexture = axisCount * inverseAxisLessTexture;
                }
            }

            // compute center of slice
            float z = -halfHeight + height * axisFraction;
            Vector3f sliceCenter = new Vector3f(0, 0, z);

            // compute slice vertices with duplication at end point
            int save = i;
            for (int radialCount = 0; radialCount < radialSamples; radialCount++, i++) {
                float radialFraction = radialCount * inverseRadial; // in [0,1)
                tempNormal.set(cos[radialCount], sin[radialCount], 0.0f);

                if (vNormals != null) {
                    vNormal = vNormals[radialCount];
                } else if (radius == radius2) {
                    vNormal = tempNormal;
                }

                if (topBottom == 0) {
                    if (!inverted)
                        nb.put(vNormal.x).put(vNormal.y).put(vNormal.z);
                    else
                        nb.put(-vNormal.x).put(-vNormal.y).put(-vNormal.z);
                } else {
                    nb.put(0).put(0).put(topBottom * (inverted ? -1 : 1));
                }

                tempNormal.multLocal((radius - radius2) * axisFraction + radius2)
                        .addLocal(sliceCenter);
                pb.put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);

                tb.put((inverted ? 1 - radialFraction : radialFraction))
                        .put(axisFractionTexture);
            }

            BufferUtils.copyInternalVector3(pb, save, i);
            BufferUtils.copyInternalVector3(nb, save, i);

            tb.put((inverted ? 0.0f : 1.0f))
                    .put(axisFractionTexture);
        }


}
