package main.java.com.eunjin.model.dto;

import java.util.Map;

public class SearchCondition {
    // 검색에 사용할 조건
    private String key;
    private String word;
    private int currentPage; // 1부터 시작하는 현재 페이지 번호
    private int itemsPerPage = 5;
    
    // key를 blockbox에서 선택할 때 사용할 Map
    private Map<String, String> keyBlackBox = Map.of("1", "name", "2", "email");
    
    public SearchCondition() {}

    
    public SearchCondition(String key, String word, int currentPage) {
        if (key != null) {
            this.key = keyBlackBox.getOrDefault(key, "");
        }
        this.word = word;
        this.currentPage = currentPage;

    }

    /**
     * 페이징을 위해 현재 페이지의 offset 반환
     * 
     * @return
     */
    public int getOffset() {
        return (currentPage - 1) * itemsPerPage;
    }

    /**
     * SearchCondition에 key와 word가 모두 설정되어있는지 확인
     * 
     * @param condition
     * @return
     */
    public boolean hasKeyword() {
        return key != null && !key.isBlank() && word != null && !word.isBlank();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public String toString() {
        return "SearchCondition [key=" + key + ", word=" + word + ", currentPage=" + currentPage + ", itemsPerPage=" + itemsPerPage + "]";
    }

}
