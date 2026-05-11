# ucutils

UCUtils ist eine Mod für den UnicaCity-Rollenspielserver, die vom früheren UnicacityAddon inspiriert wurde. Sie bietet nützliche
Befehle, Komfortfunktionen und speziell für UnicaCity entwickelte Tools, die das Gameplay verbessern und alltägliche Interaktionen
reibungsloser und angenehmer gestalten.

## Features & Funktionen

### Allgemein

- Sollte ein Command mit einem Großbuchstaben eingegeben werden (z. B. `/Afk` statt `/afk`) wird dieser Command automatisch zu einem
  gültigen Command umgewandelt
- Als Business-Besitzer wird in der Business-Info ein Button angezeigt, um die Einnahmen direkt abzubuchen
- Wirft man eine Glasflasche in der Nähe eines Shops weg, wird diese als Pfand abgegeben
- Es wird angezeigt wie lang der Cooldown für Bandagen, Schmerzpillen und Absorption ist
- Es werden Sounds abgespielt für Notrufe, Bomben, Feuer, Staatsbankraub und weitere Situationen
- Über der Hungerleiste wird der Durst angezeigt
- Mit einem Rechtsklick, während man schleicht, kann man bewusstlosen Personen Erste-Hilfe geben
- Über dem Spielernamen wird eine Information angezeigt, wenn ein Spieler AFK oder im A-Duty ist
- Für Teammitglieder wird eine Warnung angezeigt, wenn sie sich im Admindienst befinden und eine Waffe in der Hand haben

### Auto

- Beim Suchen seines Fahrzeugs (`/car find`) wird automatisch das erste Fahrzeug ausgewählt
- Werden die Koordinaten eines Autos angezeigt, wird automatisch eine Navigation zu diesen gestartet
- Das Auf-/Abschließen eines Fahrzeuges wurde teilweise automatisiert (automatisches Klicken des Items im Inventar)
- Beim Rechtsklick auf das eigene Fahrzeug wird automatisch `/car lock` ausgeführt
- Steigt man in ein Fahrzeug ein, wird dieses automatisch gestartet und abgeschlossen
- Das zuletzt gefahrene Fahrzeug wird mit einem Symbol markiert

### Fraktionen

- Für bewusstlose Spieler wird hinter dem Spielernamen ein Symbol angezeigt, um die dazugehörige Fraktion zu erkennen
- Sollte ein Spieler Contract, auf der Blacklist stehen, Hausverbot oder Wanted-Punkte haben, wird der Name dementsprechend eingefärbt
  und in der Spielerliste angezeigt
- Das Design der Reinforcements ist so überarbeitet, dass diese besser auffallen
- Für das FBI, die Polizei und den Rettungsdienst gibt es einen Timer, der die Dauer der Bombe anzeigt
- Mit einem Rechtsklick auf ein Fraktionstor (nicht Fraktionstür) wird dieses automatisch geöffnet oder geschlossen
- Für den Rettungsdienst wird der Cooldown von Bandagen und Schmerzpillen unter dem Spielernamen angezeigt
- Wenn man den Navi-Punkt eines Notrufs erreicht, wird der Notruf automatisch als erledigt markiert
- Der Fraktionschat kann individuell eingefärbt werden
- Eine Plantage kann direkt durch gleichzeitiges Schleichen und Klicken mit einem Samen in der Hand gelegt werden
- Eine Plantage kann durch einen Rechtsklick mit einem Wassereimer oder Dünger direkt gewässert beziehungsweise gedüngt werden
- Der Rettungsdienst kann durch einen Rechtsklick auf eine bewusstlose Person diese wiederbeleben

### Jobs

- Bei Transport-Jobs werden nach der Eingabe des `/droptransport` alle weiteren Kisten automatisch abgegeben
- Beim Hochseefischer-Job wird das Netz automatisch ausgeworfen und der gefangene Fisch am Ende automatisch abgegeben
- Für den Transport des Tabaks zur Shisha-Bar wird der Tabak am Ende automatisch abgegeben
- Der Müllmann-Job gibt am Ende den gesammelten Müll automatisch ab, ohne dass der `/dropwaste` Command ausgeführt werden muss
- Für den Pizzalieferanten-Job wird `/getpizza` automatisch ausgeführt, bis 10 Pizzen gesammelt wurden
- Es werden Countdowns angezeigt, bis ein Job wieder ausgeführt werden kann
- Bei der Abgabe von Uran am Atomkraftwerk muss man nicht mehr aus dem Auto aussteigen
- Aktive Mining XP-Booster werden angezeigt

### Widgets

- Aktuelles Datum und Uhrzeit
- Informationen über den PayDay (Dauer bis zum nächsten PayDay, Gehalt und Erfahrung) einschließlich Reichensteuer-Warnung
- Status des Autos (offen/abgeschlossen)
- Geld auf der Hand und auf der Bank

## Befehle

**Allgemein**

| Befehl                            | Beschreibung                                                                                                    |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `/ucutils (<sync>)`               | Zeigt nützliche Status-Informationen über das Projekt an oder startet eine Synchronisierung                     |
| `/mi`                             | Alias für `/memberinfo`                                                                                         |
| `/mia`                            | Alias für `/memberinfoall`                                                                                      |
| `/screenshot`                     | Erstellt einen Screenshot in einer bestimmten Kategorie                                                         |
| `/shutdown <friedhof\|gefängnis>` | Aktiviert das automatische Herunterfahren des PCs nachdem man nicht mehr auf dem Friedhof oder im Gefängnis ist |

**Chat**

| Befehl | Beschreibung                                                                                                   |
|--------|----------------------------------------------------------------------------------------------------------------|
| `/ff`  | Aktiviert und deaktiviert das dauerhafte Schreiben im F-Chat ohne den `/f` Befehl jedes Mal eingeben zu müssen |
| `/dd`  | Aktiviert und deaktiviert das dauerhafte Schreiben im D-Chat ohne den `/d` Befehl jedes Mal eingeben zu müssen |
| `/ww`  | Aktiviert und deaktiviert das dauerhafte Flüstern ohne den `/w` Befehl jedes Mal eingeben zu müssen            |

**Fraktionen**

| Befehl          | Beschreibung                                                                                                                                      |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `/eigenbedarf`  | Nimmt eine eingestellte Menge an Drogen aus der Drogenbank einer Fraktion oder gibt diese an einen Spieler                                        |
| `/schwarzmarkt` | Zeigt alle Schwarzmärkte an einschließlich des Zeitpunkts des letzten Besuchs des Ortes und einer Markierung ob sich der Schwarzmarkt dort befand |
| `/dealer`       | Zeigt alle Dealer an einschließlich des Zeitpunkts des letzten Besuchs des Ortes und einer Markierung ob sich der Dealer dort befand              |

**Geld**

| Befehl                 | Beschreibung                                                                                                                                                                            |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/einzahlen (<force>)` | Zahlt das gesamte Bargeld in den ATM in der Nähe ein                                                                                                                                    |
| `/adropmoney`          | Bucht für den Geldtransport-Job so viel Geld vom Konto ab, sodass das Geld vom Geldtransport-Job in den ATM eingezahlt werden kann und bucht das Geld anschließend zurück auf das Konto |

**Handy**

| Befehl                            | Beschreibung                                                             |
|-----------------------------------|--------------------------------------------------------------------------|
| `/acall <Spielername>`            | Ermöglicht das Anrufen mittels Spielername statt der Nummer              |
| `/asms <Spielername> <Nachricht>` | Ermöglicht das Schreiben einer SMS mittels Spielername statt der Nummer  |
| `/reply <Nachricht>`              | Antwortet direkt auf eine SMS                                            |
