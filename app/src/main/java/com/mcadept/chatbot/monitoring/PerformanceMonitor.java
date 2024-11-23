package com.mcadept.chatbot.monitoring;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {
    private static final String TAG = "PerformanceMonitor";
    private static final long MONITORING_INTERVAL = 5000; // 5 seconds
    private static final String PERFORMANCE_LOG_FILE = "performance_log.csv";

    private final Context context;
    private final Handler handler;
    private final AtomicLong messageCount;
    private final AtomicLong totalResponseTime;
    private long startTime;
    private boolean isMonitoring;

    private FileWriter logWriter;

    public PerformanceMonitor(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.messageCount = new AtomicLong(0);
        this.totalResponseTime = new AtomicLong(0);
        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            File logFile = new File(context.getFilesDir(), PERFORMANCE_LOG_FILE);
            logWriter = new FileWriter(logFile, true);
            if (!logFile.exists()) {
                logWriter.append("Timestamp,Memory Usage (MB),Message Count,Avg Response Time (ms),CPU Usage (%)\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error initializing log file", e);
        }
    }

    public void startMonitoring() {
        if (isMonitoring) return;
        
        isMonitoring = true;
        startTime = System.currentTimeMillis();
        scheduleNextMonitoring();
    }

    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacksAndMessages(null);
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing log file", e);
        }
    }

    private void scheduleNextMonitoring() {
        if (!isMonitoring) return;

        handler.postDelayed(() -> {
            collectMetrics();
            scheduleNextMonitoring();
        }, MONITORING_INTERVAL);
    }

    private void collectMetrics() {
        // Memory metrics
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        float totalMemory = memoryInfo.getTotalPss() / 1024f; // Convert to MB

        // Message processing metrics
        long currentMessageCount = messageCount.get();
        double avgResponseTime = currentMessageCount > 0 
            ? totalResponseTime.get() / (double) currentMessageCount 
            : 0;

        // CPU usage (approximate)
        float cpuUsage = getCpuUsage();

        // Log metrics
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        String logEntry = String.format(Locale.US, "%s,%.2f,%d,%.2f,%.2f\n",
                timestamp, totalMemory, currentMessageCount, avgResponseTime, cpuUsage);

        try {
            logWriter.append(logEntry);
            logWriter.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to log file", e);
        }
    }

    private float getCpuUsage() {
        try {
            String[] cpuStats = new String[2];
            cpuStats[0] = readCpuStats();
            Thread.sleep(100);
            cpuStats[1] = readCpuStats();

            long[] stats1 = parseCpuStats(cpuStats[0]);
            long[] stats2 = parseCpuStats(cpuStats[1]);

            long totalTime = (stats2[0] + stats2[1] + stats2[2]) - (stats1[0] + stats1[1] + stats1[2]);
            long idleTime = stats2[3] - stats1[3];

            return totalTime > 0 ? 100f * (1f - (float) idleTime / totalTime) : 0f;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating CPU usage", e);
            return 0f;
        }
    }

    private String readCpuStats() throws IOException {
        java.io.RandomAccessFile reader = new java.io.RandomAccessFile("/proc/stat", "r");
        String load = reader.readLine();
        reader.close();
        return load;
    }

    private long[] parseCpuStats(String stats) {
        String[] parts = stats.split("\\s+");
        return new long[]{
            Long.parseLong(parts[1]), // user
            Long.parseLong(parts[2]), // nice
            Long.parseLong(parts[3]), // system
            Long.parseLong(parts[4])  // idle
        };
    }

    public void recordMessageProcessing(long responseTime) {
        messageCount.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);
    }

    public void reset() {
        messageCount.set(0);
        totalResponseTime.set(0);
        startTime = System.currentTimeMillis();
    }
}
