package com.xwintop.xJavaFxTool.controller.games.sudoku;

public class Matrix {

	public int length;
	private int[][] array;

	Matrix(int row) {
		this.length = row;
		this.array = new int[row][];
	}

	Matrix(int row, int col) {
		this.length = row;
		this.array = new int[row][col];
	}

	Matrix(int[][] array) {
		this.length = array.length;
		this.array = new int[length][array[0].length];
		for (int i = 0; i < length; i++) {
			System.arraycopy(array[i], 0, this.array[i], 0, array[i].length);
		}
	}

	public void put(int row, int col, int value) {
		set(row, col, value);
	}

	public void set(int row, int[] array) {
		this.array[row] = new int[array.length];
		System.arraycopy(array, 0, this.array[row], 0, array.length);
	}

	public void set(int row, int col, int value) {
		array[row][col] = value;
	}

	public int[] get(int row) {
		return array[row];
	}

	public int get(int row, int col) {
		return array[row][col];
	}

	public int[][] toArray() {
		return array;
	}

	public void print() {
		print(" ");
	}

	public void print(String seprator) {
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				System.out.print(array[i][j] + seprator);
			}
			System.out.println();
		}
	}

}
