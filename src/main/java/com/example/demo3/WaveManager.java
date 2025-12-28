package com.example.demo3;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.application.Platform;


public class WaveManager {
    private final List<WaveData> allWaves;
    private final EnemyManager enemyManager;
    private final Text nextWaveText;
    private final Runnable onAllWavesCompleted;
    private boolean gameOver = false;
    private Timeline countdownTimeline;
    private int currentWaveIndex = 0;
    private boolean isWaveActive = false;
    private final AtomicInteger activeEnemiesCount = new AtomicInteger(0);

    public WaveManager(int currentLevel, EnemyManager manager, Text nextWaveText, Runnable onAllWavesCompleted) {
        this.enemyManager = manager;
        this.nextWaveText = nextWaveText;
        this.onAllWavesCompleted = onAllWavesCompleted;
        this.allWaves = loadWaveData(currentLevel);

        // Düşman ölüm/varış takibi için listener ekle
        manager.setEnemyStateListener(() -> {
            if (activeEnemiesCount.decrementAndGet() == 0 && !isWaveActive) {
                Platform.runLater(this::startNextWaveCountdown);
            }
        });
    }

    private List<WaveData> loadWaveData(int level) {
        List<WaveData> waves = new ArrayList<>();
        ArrayList<Integer> counts = EnemyManager.getEnemyCounts(level);
        ArrayList<Double> intervals = EnemyManager.getIntervals(level);
        ArrayList<Integer> delays = EnemyManager.getDelays(level);

        for (int i = 0; i < counts.size(); i++) {
            waves.add(new WaveData(counts.get(i), intervals.get(i), delays.get(i)));
        }
        return waves;
    }

    public void start() {
        startNextWaveCountdown();
    }

     private void startNextWaveCountdown() {
    	 if (gameOver) return;
    	    if (currentWaveIndex >= allWaves.size()) {
    	        onAllWavesCompleted.run();
    	        return;
    	    }

         WaveData wave = allWaves.get(currentWaveIndex);
         setCountdownTimeline(new Timeline());

         for (int i = wave.delay; i >= 1; i--) {
             int timeLeft = i;
             KeyFrame frame = new KeyFrame(Duration.seconds(wave.delay - i),
                     e -> nextWaveText.setText("Next Wave: " + timeLeft + "s"));
             getCountdownTimeline().getKeyFrames().add(frame);
         }

         // 0 saniye kaldı mesajı
         KeyFrame zeroFrame = new KeyFrame(Duration.seconds(wave.delay),
                 e -> nextWaveText.setText("Next Wave: 0s"));
         getCountdownTimeline().getKeyFrames().add(zeroFrame);

         // Wave başlasın
         getCountdownTimeline().setOnFinished(e -> spawnWave(wave));
         getCountdownTimeline().play();
     }
     public void setGameOver(boolean value) {
    	    this.gameOver = value;
    	}



     private void updateCountdown(WaveData wave) {
        int remaining = wave.delay - (int) (getCountdownTimeline().getCurrentTime().toSeconds());
        nextWaveText.setText("Next Wave: " + remaining + "s");
    }

    private void spawnWave(WaveData wave) {
    	 if (gameOver) return;
    	    isWaveActive = true;
    	    activeEnemiesCount.set(wave.count);

        Timeline spawnTimeline = new Timeline();
        for (int i = 0; i < wave.count; i++) {
            spawnTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i * wave.interval), e -> {
                        enemyManager.spawnEnemy();
                    })
            );
        }

        spawnTimeline.setOnFinished(e -> {
            isWaveActive = false;
            currentWaveIndex++;

            // Son düşman spawn olduğunda hemen kontrol et
            if (activeEnemiesCount.get() == 0) {
                startNextWaveCountdown();
            }
        });

        spawnTimeline.play();
    }

    public Timeline getCountdownTimeline() {
		return countdownTimeline;
	}

	public void setCountdownTimeline(Timeline countdownTimeline) {
		this.countdownTimeline = countdownTimeline;
	}

	public static class WaveData {
        final int count;
        final double interval;
        final int delay;

        public WaveData(int count, double interval, int delay) {
            this.count = count;
            this.interval = interval;
            this.delay = delay;
        }
    }
}
