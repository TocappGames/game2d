package dev.wearkit.core.engine;

import dev.wearkit.core.rendering.Renderable;

import org.dyn4j.geometry.Vector2;

import java.util.SortedSet;
import java.util.TreeSet;

public class World extends org.dyn4j.dynamics.World implements Measurable, Scene, Scalable {

    private Vector2 size;
    private SortedSet<Renderable> decoration;

    public void setScale(double scale) {
        this.scale = scale;
        this.size.x /= scale;
        this.size.y /= scale;
    }

    private double scale;

    World(double scale) {
        this.scale = scale;
        this.decoration = new TreeSet<>();
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    @Override
    public Vector2 getSize() {
        return this.size;
    }

    @Override
    public SortedSet<Renderable> getDecoration() {
        return decoration;
    }

    @Override
    public double getScale() {
        return this.scale;
    }
}