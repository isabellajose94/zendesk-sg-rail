# ZENDESK SG RAIL SYSTEM

This is a micro back-end web application that gives the best route to use MRT.
By providing starting station and destination station, it will provide useful information such as:
- Amount of stations
- Station's codes that will be passed through
- Step-by-step detailed instructions to ride MRT

*P.S. Need map to remember the MRT map -> http://journey.smrt.com.sg/journey/mrt_network_map/ (slightly different mapping, especially TE lines, refer to src/main/resources/station_map.csv)* 

## Installation guide

### Install JDK 11

#### Linux user
```
sudo apt-get install openjdk-11-jdk-headless
```

#### Windows user
Can install manually through oracle website -> *https://www.oracle.com/java/technologies/javase-jdk11-downloads.html*

### Run application

most terminal accept `./gradlew` to run gradle command, if it doesn't work can try `gradlew`

```
./gradlew bootRun
```
or
```
gradlew bootRun
```

### Build jar and run the jar

```
./gradlew bootJar
```
or 
```
gradlew bootJar
```
then run the jar (make sure your JDK is version 11)
```
java -jar build/libs/sgrail-0.0.1-SNAPSHOT.jar
```

### Run test
```
./gradlew clean test
```
or
```
gradlew clean test
```
To see test summary it can be view in `./build/reports/tests/test/index.html`

## How to use the application
This application provide an API to get best route
### Route GET API
- Method: `GET`
- URL: `/api/route/{startStation}/{endStation}`
- Path Variables:
  - `startStation` (required): The name of the starting station
  - `endStation` (require): The name of the destination station
- Query parameters:
  - `startTime` (required): The starting journey time, format -> `YYYY-MM-DDThh:mm` (e.g. `2019-01-31T16:00`)
- Success response body format:
  - Default
```
{
    "amountOfStations": Integer (e.g. 8),
    "routes": Array of String (e.g. [
        "EW12",
        "EW11",
        "EW10",
        "EW9",
        "EW8",
        "CC9",
        "CC10",
        "CC11",
        "CC12"
    ]),
    "steps": Array of String (e.g. [
        "Take EW line from Bugis to Lavender",
        "Take EW line from Lavender to Kallang",
        "Take EW line from Kallang to Aljunied",
        "Take EW line from Aljunied to Paya Lebar",
        "Change from EW line to CC line",
        "Take CC line from Paya Lebar to MacPherson",
        "Take CC line from MacPherson to Tai Seng",
        "Take CC line from Tai Seng to Bartley"
    ])
}
```
  - With time constraint (when `startTime` is filled)
```
{
    "durationTime": Integer (e.g. 150),
    "routes": Array of String (e.g. [
        "EW27",
        "EW26",
        "EW25",
        "EW24",
        "EW23",
        "EW22",
        "EW21",
        "CC22",
        "CC21",
        "CC20",
        "CC19",
        "DT9",
        "DT10",
        "DT11",
        "DT12"
    ]),
    "steps": Array of String (e.g. [
        "Take EW line from Boon Lay to Lakeside",
        "Take EW line from Lakeside to Chinese Garden",
        "Take EW line from Chinese Garden to Jurong East",
        "Take EW line from Jurong East to Clementi",
        "Take EW line from Clementi to Dover",
        "Take EW line from Dover to Buona Vista",
        "Change from EW line to CC line",
        "Take CC line from Buona Vista to Holland Village",
        "Take CC line from Holland Village to Farrer Road",
        "Take CC line from Farrer Road to Botanic Gardens",
        "Change from CC line to DT line",
        "Take DT line from Botanic Gardens to Stevens",
        "Take DT line from Stevens to Newton",
        "Take DT line from Newton to Little India"
    ])
}
```
- Fail response body format:
```
{
    "errorMessage": String (e.g. "`Bastley` end station is not found")
}
```