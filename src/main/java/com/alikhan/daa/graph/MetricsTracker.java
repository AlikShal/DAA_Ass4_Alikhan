package com.alikhan.daa.graph;

import java.io.FileWriter;
import java.io.IOException;

public class MetricsTracker {
    private long startTime;
    private long endTime;
    private int vertexCount;
    private int edgeCount;
    private int sccCount;
    private String algoName;

    public MetricsTracker(String algoName, int vertexCount, int edgeCount) {
        this.algoName = algoName;
        this.vertexCount = vertexCount;
        this.edgeCount = edgeCount;
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public void setSccCount(int sccCount) {
        this.sccCount = sccCount;
    }

    public long getElapsedMs() {
        return (endTime - startTime) / 1_000_000;
    }

    public void saveToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(String.format("%s,%d,%d,%d,%d\n",
                    algoName, vertexCount, edgeCount, sccCount, getElapsedMs()));
        } catch (IOException e) {
            System.err.println("Ошибка при записи метрик: " + e.getMessage());
        }
    }
}
