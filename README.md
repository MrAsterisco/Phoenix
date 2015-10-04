# Phoenix
As a part of one of my exams at the University, we were asked to develop a small application simulating a Car Sharing system.

Specifications of this project were strict: we had to use RMI to develop a multithread environment in Java to support this Car Sharing.

## Structure

The project is composed of three sections: **Base**, **Server** and **Client**. Even though each section describes itself with its name, we'll go through each one in the list below:

* **Base**: contains the classes shared by both *Server* and *Clients*. Also includes a small DbLayer that handles communications with SQLite.
* **Server**: instantiates and manage a Phoenix Park Sharing server, that handles car and park requests and keeps track of car availability and park statuses.
* **Client**: simulates some typical requests to the *Server*. When launched, generates a random location (inside the range of *Genoa, Italy*) and presents the user with some possibilities to query the *Server*.

## Technologies
I chose to base this project onto a small SQLite database mainly because that wasn't the main part of the exam. I was asked to use *Remote Method Invocation*, on which you can find more information [here](https://it.wikipedia.org/wiki/Remote_Method_Invocation).

Multithreading has been achieved by using *Callable* and *Runnable* objects.

## Issues
After delivering the project, I found out that RMI itself already creates a new thread for each connection. In my code, I create a new thread for each RMI thread and this is definitely not the right way to do things.

Anyway, it's a great example of what you can do with Java and multithread, so I won't fix it.

