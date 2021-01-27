package Requests;

import Results.FillResult;

public class FillRequest {

    private int generations; // Optional parameter for num generations to be filled
    private String userName;

    public FillRequest() {}

    /**
     * Creates a fill request if no number of generations are specified
     * By default, four generations will be filled.
     *
     * @param userName User's username
     */
    public FillRequest(String userName){
        this.userName = userName;
        generations = 4;
    }

    /**
     * Creates a fill request with the data provided by the fill handler
     *
     * @param generations Number of generations to fill in family tree
     * @param userName User's username
     */
    public FillRequest(String userName, int generations) {
        this.userName = userName;
        this.generations = generations;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
