package com.iisquare.etl.spark.flow;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

public abstract class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	protected SparkConf sparkConf;
	protected boolean isReady = false;
	protected Set<Node> source = new HashSet<>();
	protected Set<Node> target = new HashSet<>();
	protected Properties properties;
	protected JavaRDD<Row> result;
	protected StructType structType;
	
	public SparkConf getSparkConf() {
		return sparkConf;
	}

	public void setSparkConf(SparkConf sparkConf) {
		this.sparkConf = sparkConf;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public Set<Node> getSource() {
		return source;
	}
	
	public void setSource(Set<Node> source) {
		this.source = source;
	}
	
	public Set<Node> getTarget() {
		return target;
	}
	
	public void setTarget(Set<Node> target) {
		this.target = target;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public JavaRDD<Row> getResult() {
		return result;
	}

	public void setResult(JavaRDD<Row> result) {
		this.result = result;
	}

	public StructType getStructType() {
		return structType;
	}

	public void setStructType(StructType structType) {
		this.structType = structType;
	}

	public abstract JavaRDD<Row> process() throws Exception;
	
}
