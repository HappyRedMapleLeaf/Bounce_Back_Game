# ICS4U Final Assignment

A top-down survival-ish game set in a dense city, where the goal is to obtain a stable income, submitted in June 2023 as the final assignment for my grade 12 computer science course.

The game uses Processing, which our teachers used to teach us about graphics in Java. To run with VSCode, download and unzip Processing and add ..\core\library\core.jar as a referenced library.
https://processing.org/download

The main technical challenge with this game was the collision detection and bouncing of the circular player hitbox with the tile-based world, achieved (unfortunately not perfectly) with the help of this video: https://www.youtube.com/watch?v=D2a5fHX-Qrs
The level is also randomly generated and practically infinite, using some admittedly questionable techniques. The player can load and save the game, where progress made in the world is stored in "chunks" (to avoid needing to save a very, very large array) similar to the system used in games like Minecraft.
