package com.noudzandbergen.hva.racequest.components;

import com.noudzandbergen.hva.racequest.Car;
import com.noudzandbergen.hva.racequest.ParkingGrid;
import com.noudzandbergen.hva.racequest.RaceQuest;
import com.noudzandbergen.hva.racequest.Shape;
import com.noudzandbergen.hva.racequest.util.Util;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class ParkingGridComponent extends ParkingGrid {

	private final RaceQuest game;
	private Shape shape;
	private Car car;
	private int rotation, x, y;

	public ParkingGridComponent(RaceQuest game, int columns, int rows) {
		super(columns, rows);
		this.game = game;

		reset();
	}

	public void update(float delta) {

	}

	public void place() {
		for (Shape.Position pos : shape.getPositions(rotation))
			set(pos.x + x, pos.y + y, car);

		ArrayList<RemovedCarRow> removedRows = removeFullRows();
		game.score += removedRows.size();
	}

	public void reset() {
		shape = Util.getRandomItem(game.shapes);
		car = Util.getRandomItem(game.cars);
		x = columns / 2;
		y = 0;
	}

	public void move(int dx, int dy) {
		int prevX = x, prevY = y;
		x += dx;
		y += dy;
		if (checkShapeCollision()) {
			x = prevX;
			y = prevY;
			if (dy > 0) {
				place();
				reset();
			}
		}
	}

	public void rotate(int value) {
		int prevRotation = rotation;
		rotation += value;
		if (checkShapeCollision())
			rotation = prevRotation;
	}

	protected boolean checkShapeCollision() {
		for (Shape.Position pos : shape.getPositions(rotation))
			if (checkCollision(pos.x + this.x, pos.y + this.y))
				return true;
		return false;
	}

	public void draw(PGraphics g, PVector size, float time) {

		g.pushMatrix();

		float cellWidth = size.x / columns;
		float cellHeight = size.y / rows;

		g.fill(0xff2f1f27);
		g.rect(0, 0, size.x, size.y);

		// Render tetris car grid
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				Car car = get(x, y);

				if (car == null)
					continue;

				g.pushMatrix();
				g.translate((x + .5f) * cellWidth, (y + .5f) * cellHeight);
				g.scale(cellHeight / 6, cellHeight / 6);
				g.rotate(PConstants.PI);
				car.draw(g);
				g.popMatrix();
			}
		}

		Shape.Position[] rot = shape.getPositions(rotation);
		for (Shape.Position position : rot) {
			int x = position.x + this.x;
			int y = position.y + this.y;
			g.pushMatrix();
			g.translate((x + .5f) * cellWidth, (y + .5f) * cellHeight);
			g.scale(cellHeight / 6, cellHeight / 6);
			g.rotate(PConstants.PI);
			car.draw(g);
			g.popMatrix();
		}

		g.popMatrix();
	}

}
