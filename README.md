# Connect6PerfectPlayAI

A Java-based AI agent for the Connect-6 game, implementing a perfect-play strategy using threat-space search and bitboard techniques.

## Overview

This project features an AI designed to play Connect-6 optimally on an 8x12 board. It utilizes precomputed winning masks and bitwise operations to evaluate game states efficiently. The AI follows a strategic hierarchy:

1. Attempt to win immediately.
2. Block opponent's winning moves.
3. Create fork opportunities.
4. Force opponent into specific replies.
5. Apply heuristic-based decisions.([Oracle][1], [Nerd Wisdom][2])

## Features

* **Threat-Space Search**: Analyzes potential threats to determine optimal moves.
* **Bitboard Representation**: Efficiently represents the game board using `BigInteger`.
* **Precomputed Winning Masks**: Stores all possible winning combinations for quick access.
* **Strategic Decision-Making**: Implements a hierarchy of strategies to choose the best move.

## Installation

1. Ensure you have Java 8 or higher installed.
2. Clone the repository:

   ```bash
   git clone https://github.com/akshatgupta-dev/Connect6PerfectPlayAI.git
   ```


3\. Navigate to the project directory:

```bash
cd Connect6PerfectPlayAI
```


4\. Compile the project:

```bash
javac -d bin src/ch/qc/starter/*.java
```



## Usage

To run the AI:([GitHub][3])

```bash
java -cp bin ch.qc.starter.Connect6PerfectPlayAI
```



Ensure that the `QuantumField` and related classes from the `ch.cern.quantumconnect.core` package are available in your classpath.

## Project Structure

* `src/ch/qc/starter/Connect6PerfectPlayAI.java`: Main AI implementation.
* `src/ch/qc/starter/RandomBot.java`: A simple AI that selects moves randomly.

## Dependencies

* Java Standard Library (`java.math.BigInteger`, `java.util.*`)
* `ch.cern.quantumconnect.core` package (ensure it's included in your project)

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

