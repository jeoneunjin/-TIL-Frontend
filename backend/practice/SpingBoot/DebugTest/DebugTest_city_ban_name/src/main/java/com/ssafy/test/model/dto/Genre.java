package com.ssafy.test.model.dto;

public class Genre {

	private int genreId;
	private String name;

	public Genre() {
	}

	public Genre(int genreId, String name) {
		this.genreId = genreId;
		this.name = name;
	}

	public int getGenreId() {
		return genreId;
	}

	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Genre [genreId=" + genreId + ", name=" + name + "]";
	}

}
