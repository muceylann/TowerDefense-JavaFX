# 🏰 Tower Defense Game (JavaFX)

Marmara Üniversitesi Bilgisayar Mühendisliği (İngilizce) bölümü **CSE 1242 Computer Programming II** dersi bitirme projesi.

## 🎮 Proje Özeti
Bu oyun, oyuncuların kuleler yerleştirerek düşman dalgalarına karşı savunduğu bir strateji oyunudur. Oyunun temel amacı, 5 düşmanın bitiş noktasına ulaşmasını engelleyerek tüm dalgaları hayatta kalarak tamamlamaktır.

## 🚀 Teknik Detaylar
- **Dil:** Java (JDK 17+)
- **Kütüphane:** JavaFX
- **Arayüz:** Proje isterleri doğrultusunda **Scene Builder kullanılmadan**, tamamen Java kodu ile (programmatic UI) geliştirilmiştir.
- **Mimari:** Nesne Yönelimli Programlama (OOP) prensipleri üzerine inşa edilmiştir.

## 🛠️ Oyun Mekanikleri
- **Kule Tipleri:**
  - `Single Shot Tower`: Tek bir hedefe mermi atar.
  - `Laser Tower`: Menzildeki tüm hedeflere sürekli hasar verir.
  - `Triple Shot Tower`: Aynı anda üç düşmana ateş eder.
  - `Missile Launcher`: Alan hasarı (AoE) veren füzeler fırlatır.
- **Düşmanlar:** Sağlık barları ve elenince tetiklenen parçacık (explosion) efektleri mevcuttur.
- **Harita Sistemi:** 5 farklı seviye haritası, `.txt` dosyalarından dinamik olarak okunarak yüklenir.

## 📂 Dosya Yapısı
- `Enemy.java`: Düşman yapay zekası ve hareket mantığı.
- `Tower.java` & `TowerType.java`: Savunma sistemleri sınıfları.
- `GameMaps.java`: Harita yükleme ve ızgara sistemi.
- `WaveManager.java`: Düşman dalgalarının zamanlaması.

## 🔧 Kurulum
1. Repoyu klonlayın: `git clone https://github.com/muceylann/TowerDefense-JavaFX.git`
2. IntelliJ IDEA ile projeyi açın.
3. JavaFX kütüphanelerini tanıtın.
4. `HelloApplication.java` dosyasını çalıştırın.
