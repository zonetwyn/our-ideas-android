package com.zonetwyn.projects.ourideas.payloads;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zonetwyn.projects.ourideas.models.Idea;

import java.util.List;

public class IdeaResponse {

    @SerializedName("docs")
    @Expose
    private List<Idea> ideas = null;
    @SerializedName("totalDocs")
    @Expose
    private int totalDocs;
    @SerializedName("limit")
    @Expose
    private int limit;
    @SerializedName("hasPrevPage")
    @Expose
    private boolean hasPrevPage;
    @SerializedName("hasNextPage")
    @Expose
    private boolean hasNextPage;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("totalPages")
    @Expose
    private int totalPages;
    @SerializedName("pagingCounter")
    @Expose
    private int pagingCounter;
    @SerializedName("prevPage")
    @Expose
    private Object prevPage;
    @SerializedName("nextPage")
    @Expose
    private Object nextPage;

    public IdeaResponse() {

    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public int getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(int totalDocs) {
        this.totalDocs = totalDocs;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public void setHasPrevPage(boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPagingCounter() {
        return pagingCounter;
    }

    public void setPagingCounter(int pagingCounter) {
        this.pagingCounter = pagingCounter;
    }

    public Object getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(Object prevPage) {
        this.prevPage = prevPage;
    }

    public Object getNextPage() {
        return nextPage;
    }

    public void setNextPage(Object nextPage) {
        this.nextPage = nextPage;
    }
}
