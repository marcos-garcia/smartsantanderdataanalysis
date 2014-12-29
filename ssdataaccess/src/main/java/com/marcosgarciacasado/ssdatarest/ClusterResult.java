package com.marcosgarciacasado.ssdatarest;

public class ClusterResult {

    private final String day;
    private final Integer clusterid;

    public ClusterResult(String day, Integer clusterid) {
        this.day = day;
        this.clusterid = clusterid;
    }

	public String getDay() {
		return day;
	}

	public Integer getClusterid() {
		return clusterid;
	}
    
}
