package com.ssafy.test.model.dto;

public class Content {
	 private String code;      
	    private String title;   
	    private int genre;     
	    private String genreName;
	    private String album;   
	    private String artist;   
	    private String userId; 
	    
	    public Content() {
			
		}

		public Content(String code, String title, int genre, String genreName, String album, String artist,
				String userId) {
			super();
			this.code = code;
			this.title = title;
			this.genre = genre;
			this.genreName = genreName;
			this.album = album;
			this.artist = artist;
			this.userId = userId;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getGenre() {
			return genre;
		}

		public void setGenre(int genre) {
			this.genre = genre;
		}

		public String getGenreName() {
			return genreName;
		}

		public void setGenreName(String genreName) {
			this.genreName = genreName;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public String getArtist() {
			return artist;
		}

		public void setArtist(String artist) {
			this.artist = artist;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		@Override
		public String toString() {
			return "Content [code=" + code + ", title=" + title + ", genre=" + genre + ", genreName=" + genreName
					+ ", album=" + album + ", artist=" + artist + ", userId=" + userId + "]";
		}


	    
	
	
}
