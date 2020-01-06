package com.xwintop.xJavaFxTool.controller.games.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * 利用 Dancing links X 算法解决精确度覆盖问题。
 * <p>
 * 接收一个仅包含0和1的稀疏矩阵，从中选择某些行使每列有且仅有一个“1”覆盖。
 */
public class DLX {

	public int maxAnsNumber;
	public Matrix sparseMatrix;
	private Node root;
	private List<Node> nodes;
	private List<Matrix> ans;

	public DLX() {
		this.maxAnsNumber = 2;
		this.nodes = new ArrayList<Node>();
		this.ans = new ArrayList<Matrix>();
	}

	public DLX(Matrix sparseMatrix) {
		this();
		this.sparseMatrix = sparseMatrix;
	}

	/**
	 * 通过稀疏矩阵建立交叉十字循环双向链，并搜索精确度覆盖解。
	 *
	 * @param maxAnsNumber
	 *            Max ans number, default all
	 */
	public void solve(int maxAnsNumber) {
		this.maxAnsNumber = maxAnsNumber;
		solve();
	}

	public void solve() {
		ans.clear();
		createDancingLinks(sparseMatrix);
		search(0);
	}

	private void createDancingLinks(Matrix sparseMatrix) {
		// root && header
		root = new Node(0, 0);
		Node header = root;
		for (int i = 0; i < sparseMatrix.get(0).length; i++) {
			header.right = new Node(0, i + 1);
			header.right.left = header;
			header.header = header;
			header = header.right;
		}
		header.right = root;
		header.right.left = header;

		// sparse matrix
		for (int row = 0; row < sparseMatrix.length; row++) {
			header = root.right;
			Node first = null;
			Node last = null;
			for (int col = 0; col < sparseMatrix.get(0).length; col++) {
				if (sparseMatrix.get(row, col) == 1) {
					Node node = header;
					while (node.down != null) {
						node = node.down;
					}
					node.down = new Node(header, row + 1, col + 1);
					node.down.up = node;
					if (first == null) {
						first = node.down;
					}
					node.down.left = last;
					if (last != null) {
						node.down.left.right = node.down;
					}
					last = node.down;
					header.size++;
				}
				header = header.right;
			}
			if (last != null) {
				first.left = last;
				first.left.right = first;
			}
		}

		header = root.right;
		for (int col = 0; col < sparseMatrix.get(0).length; col++) {
			Node node = header;
			while (node.down != null) {
				node = node.down;
			}
			node.down = header;
			node.down.up = node;
			header = header.right;
		}
	}

	private void search(int k) {
		if (root.right == root) {
			ans.add(nodesToMatrix(sparseMatrix, nodes));
			return;
		}

		Node c = choose();
		cover(c);

		for (Node x = c.down; x != c; x = x.down) {
			if (nodes.size() > k) {
				nodes.remove(k);
			}
			nodes.add(k, x);
			for (Node y = x.right; y != x; y = y.right) {
				cover(y.header);
			}

			search(k + 1);

			if (ans.size() >= maxAnsNumber) return;

			// uncover
			Node xr = nodes.get(k);
			for (Node yr = xr.left; yr != xr; yr = yr.left) {
				uncover(yr.header);
			}
		}
		uncover(c);
	}

	// choose column with least 1
	private Node choose() {
		Node min = root.right;
		for (Node header = min.right; header != root; header = header.right) {
			if (min.size > header.size) {
				min = header;
			}
		}
		return min;
	}

	private void cover(Node c) {
		c.left.right = c.right;
		c.right.left = c.left;
		for (Node x = c.down; x != c; x = x.down) {
			for (Node y = x.right; y != x; y = y.right) {
				y.up.down = y.down;
				y.down.up = y.up;
				y.header.size--;
			}
		}
	}

	private void uncover(Node c) {
		c.left.right = c;
		c.right.left = c;
		for (Node x = c.up; x != c; x = x.up) {
			for (Node y = x.left; y != x; y = y.left) {
				y.up.down = y;
				y.down.up = y;
				y.header.size++;
			}
		}
	}

	private Matrix nodesToMatrix(Matrix sparseMatrix, List<Node> nodes) {
		Matrix matrix = new Matrix(nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			int line = nodes.get(i).row - 1;
			matrix.set(i, sparseMatrix.get(line));
		}
		return matrix;
	}

	public List<Matrix> getAns() {
		return ans;
	}
}

/**
 * Node类。辅助DLX算法解决精确度覆盖问题。
 * <p>
 * Node类一般包含上下左右行列及header七个属性，第一行辅助元素为特殊Node，即ColumnNode（可用继承实现），另包含size属性，
 * 表示所在列中1的个数。每个Node的header属性指向所在列的第一行辅助元素ColumnNode，ColumnNode指向自身。
 */
class Node {
	int size = 0;
	int row, col; // col for debug
	Node up, down, left, right, header;

	Node(int row, int col) {
		this.row = row;
		this.col = col;
	}

	Node(Node header, int row, int col) {
		this(row, col);
		this.header = header;
	}
}