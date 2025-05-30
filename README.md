# Java Collaborative Drawing Program
*Marshall Carey-Matthews*
## Description
This was the final assignment for Dartmouth's COSC10 course, "Problem Solving via Object-Oriented Programming." I was tasked with turning scaffold code into a collaborative server-client drawing program, similar in functionality to Google Docs.
## How It Works
*Description from the assignment:*
Each client editor has a thread for talking to the sketch server, along with a main thread for user interaction (previously, getting console input; now, handling the drawing). The server has a main thread to get the incoming requests to join the shared sketch, along with separate threads for communicating with the clients. The client tells the server about its user's drawing actions. The server then tells all the clients about all the drawing actions of each of them. There is one twist, to make this work nicely. A client doesn't just do the drawing actions on its own. Instead, it requests the server for permission to do an action, and the server then tells all the clients (including the requester) to do it. So rather than actually recoloring a shape, the client tells the server "I'd like to color this shape that color", and the server then tells all the clients to do just that. That way, the server has the one unified, consistent global view of the sketch, and keeps all the clients informed.
