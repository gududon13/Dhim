/**package com.leophysics.dhim3drenderer;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.*;
import com.badlogic.gdx.math.*;

public class SurfacePlot3d implements ApplicationListener {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelInstance modelInstance;
    private Mesh mesh;
    private Material material;

    private int width = 100;
    private int height = 100;
    private float xStart = -5f;
    private float xRange = 10f;
    private float zStart = -5f;
    private float zRange = 10f;
    private float[][] noise = new float[width][height];

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 5f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        modelBatch = new ModelBatch();

        createNoise();
        createMesh();
        createModel();
    }

    private void createNoise() {
        noise[0][0]=3f;
        
    }

    private void createMesh() {
		VertexAttributes attributes = new VertexAttributes(
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

		MeshBuilder meshBuilder = new MeshBuilder();
		meshBuilder.begin(attributes);

		Vector3 tmpVec = new Vector3();
		Color tmpColor = new Color();

		for (int x = 0; x < width - 1; x++) {
			for (int z = 0; z < height - 1; z++) {
				float x1 = xStart + x * (xRange / (float) (width - 1));
				float z1 = zStart + z * (zRange / (float) (height - 1));
				float x2 = xStart + (x + 1) * (xRange / (float) (width - 1));
				float z2 = zStart + (z + 1) * (zRange / (float) (height - 1));

				VertexInfo v1 = new VertexInfo();
				v1.setPos(x1, noise[x][z], z1);
				v1.setCol(tmpColor.set(Color.WHITE));
				meshBuilder.vertex(v1);

				VertexInfo v2 = new VertexInfo();
				v2.setPos(x1, noise[x][z + 1], z2);
				v2.setCol(tmpColor.set(Color.WHITE));
				meshBuilder.vertex(v2);

				VertexInfo v3 = new VertexInfo();
				v3.setPos(x2, noise[x + 1][z + 1], z2);
				v3.setCol(tmpColor.set(Color.WHITE));
				meshBuilder.vertex(v3);

				VertexInfo v4 = new VertexInfo();
				v4.setPos(x2, noise[x + 1][z], z1);
				v4.setCol(tmpColor.set(Color.WHITE));
				meshBuilder.vertex(v4);

				meshBuilder.rect(v1, v2, v3, v4);
			}
		}

		mesh=meshBuilder.end();
		
	}
	

	private void createModel() {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder = modelBuilder.part("grid", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked, new Material());
		meshBuilder.setVertexTransform(new Matrix4().translate(-width / 2f, 0, -height / 2f));

		
		meshBuilder.addMesh(mesh);

		Model model = modelBuilder.end();

		modelInstance = new ModelInstance(model);
	}
	

	private short[] createIndices() {
		short[] indices = new short[(width - 1) * (height - 1) * 6];
		int i = 0;
		for (int x = 0; x < width - 1; x++) {
			for (int z = 0; z < height - 1; z++) {
				short v1 = (short) (x * height + z);
				short v2 = (short) ((x + 1) * height + z);
				short v3 = (short) ((x + 1) * height + z + 1);
				short v4 = (short) (x * height + z + 1);
				indices[i++] = v1;
				indices[i++] = v2;
				indices[i++] = v3;
				indices[i++] = v3;
				indices[i++] = v4;
				indices[i++] = v1;
			}
		}
		return indices;
	}

	
	

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);
		modelBatch.render(modelInstance);
		modelBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		
		mesh.dispose();
	}
	}
**/
