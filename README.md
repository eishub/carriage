# The Carriage Environment

<img align="right" src="https://github.com/eishub/carriage/wiki/carriage.png"/>

The carriage environment consists of a *carriage* and *two robots* that can either *push* the carriage or do nothing, i.e., *wait*. The robots and the carriage are positioned on a ring with only three positions (top, left and right bottom). The robots, moreover, are each located on opposite sides of the carriage and if both push at the same time, the carriage does not move. If one robot pushes and the other robot waits, then the carriage moves one position.

The question is *which combination of strategies allows the robots to move to a particular position on the ring* (and stay there), or which strategies allow the robots to perform even more complicated dances on the ring (e.g., move to the top position, move to the left bottom position, and via the top position move to the right bottom position).

## Actions

The actions that a robot can perform are either a **push** or a **wait** action.

## Percept

The robots receive a percept of the form **carriagePos(X)** where `X` is either 0, 1, or 2.


# Releases

Releases can be found in eishub's maven repository [here](https://github.com/eishub/mvn-repo/tree/master/eishub/carriage).

Dependency information 
=====================

```
<repository>
 <id>eishub-mvn-repo</id>
 <url>https://raw.github.com/eishub/mvn-repo/master</url>
</repository>
```
	
```	
<dependency>
	<groupId>eishub</groupId>
	<artifactId>carriage</artifactId>
	<version>1.0.1-SNAPSHOT</version>
</dependency>
```

