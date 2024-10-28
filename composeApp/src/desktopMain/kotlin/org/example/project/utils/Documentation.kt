package org.example.project.utils

val documentationText = "Aplikacja pozwala na tworzenie wielokątów, w których segmenty mogą mieć różne relacje:\n" +
        "Linie proste bez dodatkowych relacji.\n" +
        "Linie pionowe i poziome wymuszające odpowiednio pionową lub poziomą orientację segmentu.\n" +
        "Linie o ustalonej długości zapewniają stałą odległość między dwoma wierzchołkami.\n" +
        "Krzywe Beziera trzeciego stopnia, które umożliwiają tworzenie gładkich zakrzywionych kształtów między wierzchołkami.\n" +
        "2. Przesuwanie Wierzchołków i Zachowanie Relacji\n" +
        "Przesuwanie wierzchołków wywołuje automatyczne dostosowanie segmentów sąsiadujących, aby zachować zadane relacje:\n" +
        "correctToTheLeft i correctToTheRight: funkcje te zapewniają poprawne dostosowanie segmentów wielokąta po lewej lub prawej stronie przesuwanego wierzchołka, aby zachować relacje, takie jak ciągłość C1 lub G1 na segmentach Beziera.\n" +
        "W przypadku segmentów o relacjach prostokątnych (poziome i pionowe) przesunięcia są ograniczane odpowiednio tylko do jednej osi.\n" +
        "Gdy segment jest krzywą Beziera, ciągłość relacji C1 lub G1 między sąsiadującymi segmentami jest zachowana przez automatyczne przesunięcie punktów kontrolnych krzywej.\n" +
        "3. Obsługa Zależności Ciągłości\n" +
        "Aplikacja obsługuje różne klasy ciągłości na krzywych Beziera:\n" +
        "Ciągłość C1: wymaga, aby krzywe miały zgodne styczne w punktach łączenia, co daje płynne przejście między segmentami.\n" +
        "Ciągłość G1: zapewnia zgodność kierunków stycznych, ale nie wymaga zgodności długości wektorów stycznych.\n" +
        "G0 (brak ciągłości): segmenty mogą nie mieć gładkiego przejścia w punktach łączenia.\n" +
        "4. Interfejs Użytkownika\n" +
        "Aplikacja dostarcza kontekstowe menu, które umożliwia użytkownikowi wykonywanie działań na wybranych punktach lub segmentach:\n" +
        "Dodawanie/Usuwanie punktów i segmentów.\n" +
        "Zmiana relacji pomiędzy segmentami (np. ustawianie segmentu na krzywą Beziera lub linię prostą).\n" +
        "Przełączanie algorytmu rysowania (Bresenham lub Wu) dla wybranych segmentów, co pozwala na lepszą kontrolę nad jakością rysowanych linii.\n" +
        "5. Zapis i Wczytywanie Wielokątów\n" +
        "Użytkownik może zapisywać i wczytywać konfigurację wielokąta, co pozwala na odtworzenie wcześniej narysowanych kształtów z zachowaniem wszystkich ustawień punktów kontrolnych i relacji segmentów.\n" +
        "Przykładowe Funkcje\n" +
        "moveAll(): Funkcja do przesunięcia całego wielokąta po płótnie.\n" +
        "drawCubicBezierBrasenham() i drawCubicBezierWu(): Rysują krzywe Beziera przy użyciu odpowiednich algorytmów rysowania.\n" +
        "correctToTheRight() i correctToTheLeft(): Zapewniają aktualizację sąsiadujących segmentów, aby zachować ciągłość i relacje w wielokącie.\n"