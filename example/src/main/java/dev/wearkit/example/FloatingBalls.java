package dev.wearkit.example;

import android.graphics.Color;
import android.graphics.Paint;

import dev.wearkit.core.rendering.Body;
import dev.wearkit.core.rendering.Thing;
import dev.wearkit.core.engine.AbstractGame;
import dev.wearkit.core.rendering.shape.Circle;
import dev.wearkit.core.rendering.shape.Rectangle;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class FloatingBalls extends AbstractGame {

    private static final String TAG = "FloatingBalls";

    @Override
    public void init() {
        world.setScale(100.0);

        double borderWidth = world.getSize().x / 50;

        Paint bgCircleColor = new Paint();
        bgCircleColor.setColor(Color.GRAY);
        Thing bg = new Thing(bgCircleColor, -2);
        double radius = Math.min(world.getSize().x, world.getSize().y) / 2 - borderWidth;
        Circle bgCentre = new Circle(radius);
        bgCentre.translate(world.getSize().x / 2, world.getSize().y / 2);
        bg.addFixture(bgCentre);
        this.world.getDecoration().add(bg);

        Paint bgCircleColor2 = new Paint();
        bgCircleColor2.setColor(Color.BLUE);
        Thing bg2 = new Thing(bgCircleColor2, -3);
        Circle bgCentre2 = new Circle(Math.min(world.getSize().x, world.getSize().y) / 4);
        bgCentre2.translate(world.getSize().x / 2, world.getSize().y / 2 - radius);
        bg2.addFixture(bgCentre2);

        this.world.getDecoration().add(bg2);


        this.world.setGravity(World.ZERO_GRAVITY);
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);

        Body frame = new Body(paint);
        Rectangle floor = new Rectangle(world.getSize().x, borderWidth);
        floor.translate(world.getSize().x / 2, world.getSize().y - borderWidth / 2);

        Rectangle ceiling = new Rectangle(world.getSize().x, borderWidth);
        ceiling.translate(world.getSize().x / 2, borderWidth / 2);

        Rectangle left = new Rectangle(borderWidth, world.getSize().y);
        left.translate(borderWidth / 2, world.getSize().y / 2);

        Rectangle right = new Rectangle(borderWidth, world.getSize().y);
        right.translate(world.getSize().x - borderWidth / 2, world.getSize().y / 2);

        frame.addFixture(floor);
        frame.addFixture(ceiling);
        frame.addFixture(left);
        frame.addFixture(right);
        frame.setMass(MassType.INFINITE);

        this.world.addBody(frame);

        for(int i = 0; i < 50; i++){
            addRandomBall();
        }

    }

    private int getRandomByte(){
        double n = Math.random() * 255;
        return (int) Math.round(n);
    }

    private void addRandomBall(){

        Paint paint = new Paint();
        paint.setColor(Color.rgb(getRandomByte(),getRandomByte(),getRandomByte()));

        Body ball = new Body(paint);
        ball.addFixture(new Circle(0.1), 1.0, 0.0, 1.0);
        ball.translate(world.getSize().x / 2, world.getSize().y / 2);
        ball.setMass(MassType.NORMAL);
        ball.setLinearVelocity(new Vector2((Math.random() - 0.5) * 20, (Math.random() - 0.5) * 20));

        this.world.addBody(ball);
    }


    @Override
    public void update() {
    }

    @Override
    public void finish() {

    }

}