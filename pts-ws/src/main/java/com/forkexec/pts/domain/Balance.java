package com.forkexec.pts.domain;

public class Balance {
	private int points = 0;
	private int seq = 0;

	public Balance() {}

	public Balance(int points, int seq) {
		this.points = points;
		this.seq = seq;
	}

	/**
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * @return the seq
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(int seq) {
		this.seq = seq;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(int points) {
		this.points = points;
	}
}