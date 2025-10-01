package sp.sponge.util.objects;

import org.joml.Vector3f;

public class BoundingBox {
    private AABB original;
    private AABB rotated;

    public BoundingBox(Vector3f min, Vector3f max) {
        this.original = new AABB(min, max);
        this.rotated = this.original;
    }

    public void updateRotatedBoundingBox() {
        
    }


    private class AABB {
        private Vector3f min;
        private Vector3f max;

        public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
        }

        public AABB(Vector3f min, Vector3f max) {
            this.min = min;
            this.max = max;
        }
    }
}
