# ucutils

UCUtils ist eine Mod für den UnicaCity-Rollenspielserver, die vom früheren UnicacityAddon inspiriert wurde. Sie bietet nützliche
Befehle, Komfortfunktionen und speziell für UnicaCity entwickelte Tools, die das Gameplay verbessern und alltägliche Interaktionen
reibungsloser und angenehmer gestalten.

## Features & Funktionen

### Allgemein

- Sollte ein Command mit einem Großbuchstaben eingegeben werden (z. B. `/Afk` statt `/afk`) wird dieser Command automatisch zu einem
  gültigen Command umgewandelt
- Statt `/navi Haus:<Hausnummer>` kann `/navi <Hausnummer>` genutzt werden
- Als Business-Besitzer wird in der Business-Info ein Button angezeigt, um die Einnahmen direkt abzubuchen
- Wirft man eine Glasflasche in der Nähe eines Shops weg, wird diese als Pfand abgegeben
- Es wird angezeigt, wie lang der Cooldown für Bandagen und Schmerzpillen ist
- Es werden Sounds abgespielt für Notrufe, Bomben, Feuer, Staatsbankraub und weitere Situationen
- Über der Hungerleiste wird der Durst angezeigt
- Über den Spielernamen wird AFK angezeigt, wenn der Spieler AFK ist
- Für Teammitglieder wird eine Warnung angezeigt, wenn sie sich im Admindienst befinden und eine Waffe in der Hand haben
- Bei der Mieterübersicht wird angezeigt wie lang ein Mieter offline ist und ein Button um diesen zu kündigen
- Beim Beten wird nach 15 Sekunden automatisch der zweite Befehl ausgeführt
- Es gibt Benachrichtigungen, wenn ein Spieler den Server betritt oder verlässt, einen Report betritt oder verlässt, den Baumodus
  betritt oder verlässt und den Admindienst betritt oder verlässt

### Auto

- Beim Suchen seines Fahrzeugs (`/car find`) wird automatisch das erste Fahrzeug ausgewählt
- Werden die Koordinaten eines Autos angezeigt, wird automatisch eine Navigation zu diesen gestartet

Folgende Funktionen sind nur für Spieler ohne Premium-Rang verfügbar, da Spieler mit Premium diese Funktion vom Server aus können:
- Das Auf-/Abschließen eines Fahrzeuges wurde teilweise automatisiert (automatisches Klicken des Items im Inventar)
- Beim Rechtsklick auf das eigene Fahrzeug wird automatisch `/car lock` ausgeführt
- Steigt man in ein Fahrzeug ein, wird dieses automatisch gestartet und abgeschlossen
- Das zuletzt gefahrene Fahrzeug wird mit einem Symbol markiert

### Fraktionen

- Für bewusstlose Spieler wird hinter dem Spielernamen ein Symbol angezeigt, um die dazugehörige Fraktion zu erkennen
- Sollte ein Spieler Wanted-Punkte haben, wird der Name der Leiche dementsprechend eingefärbt
- Das Design der Reinforcements ist so überarbeitet, dass diese besser auffallen
- Für das FBI, die Polizei und den Rettungsdienst gibt es einen Timer, der die Dauer der Bombe anzeigt
- Für den Rettungsdienst wird der Cooldown von Bandagen und Schmerzpillen unter dem Spielernamen angezeigt
- Der Fraktionschat kann individuell eingefärbt werden
- Mit `/fbank einzahlen <Betrag> <Grund>` kann das Eingabe GUI des Servers übersprungen werden
- Für den Rettungsdienst gibt es im Herstellungs-Inventar für Medikamente einen Button, um die benötigte Anzahl an Stoffen in den
  Fraktionschat zu senden
- Für den Dealer und Schwarzmarkt wird (in der Übersicht) angezeigt, an welchem Ort der Händler gefunden wurde

### Jobs

- Bei Transport-Jobs werden nach der Eingabe des `/droptransport` alle weiteren Kisten automatisch abgegeben
- Beim Hochseefischer-Job wird das Netz automatisch ausgeworfen und der gefangene Fisch am Ende automatisch abgegeben
- Für den Transport des Tabaks zur Shisha-Bar wird der Tabak am Ende automatisch abgegeben
- Der Müllmann-Job gibt am Ende den gesammelten Müll automatisch ab, ohne dass der `/dropwaste` Command ausgeführt werden muss
- Für den Pizzalieferanten-Job wird `/getpizza` automatisch ausgeführt, bis 10 Pizzen gesammelt wurden
- Es werden Countdowns angezeigt, bis ein Job wieder ausgeführt werden kann
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
| `/screenshot`                     | Erstellt einen Screenshot in einer bestimmten Kategorie                                                         |
| `/shutdown <friedhof\|gefängnis>` | Aktiviert das automatische Herunterfahren des PCs nachdem man nicht mehr auf dem Friedhof oder im Gefängnis ist |

**Chat**

| Befehl | Beschreibung                                                                                                   |
|--------|----------------------------------------------------------------------------------------------------------------|
| `/ff`  | Aktiviert und deaktiviert das dauerhafte Schreiben im F-Chat ohne den `/f` Befehl jedes Mal eingeben zu müssen |
| `/dd`  | Aktiviert und deaktiviert das dauerhafte Schreiben im D-Chat ohne den `/d` Befehl jedes Mal eingeben zu müssen |
| `/ww`  | Aktiviert und deaktiviert das dauerhafte Flüstern ohne den `/w` Befehl jedes Mal eingeben zu müssen            |
