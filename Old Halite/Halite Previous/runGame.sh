#!/bin/bash

javac ./Users/jiatinglu/Desktop/Halite-Java-Starter-Package/MyBot.java
javac ./Users/jiatinglu/Desktop/Halite-Java-Starter-Package/RandomBot.java
./halite -d "30 30" "java MyBot" "java RandomBot"
