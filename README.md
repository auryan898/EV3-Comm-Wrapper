# EV3-Comm-Wrapper

A set of wrapper classes to handle remote communications between ev3, nxt, and pc using lejos, an eclipse project.

I designed this project as a helper tool to control the EV3 brick while prototyping builds.  It is meant to be 
highly versatile and user-friendly.  The information that is communicated consists of only bytes, so that is a bit
of a hurdle to get past, but the wrapper classes should make the implementation much quicker, and allow for more 
focus on functionality, rather than the logistics of organizing of how this or that thread should be organized
in the code.  

Documentation - (TBD)  
Example Code  - (WIP)  
Demonstration - (TBA)  
Lesson Plan?  - (meh)  

### Installation

Requires JDK 7 to run on the EV3 (but 7+ on pc) and the LeJos libraries.

### Story

I originally designed this code during a university course that uses ev3 sets to teach design principles.  It wasn't
required for the course but I had a background with remote controlling robots and I felt like it could be a useful tool
for debugging.  After I've gone through most of the coding, I realized that it's kind of a hassle to debug `this` code
AND my lab code, so it's not exactly the best tool to help in fast prototyping.  Nevertheless, having the interface and
wrapper classes all set and ready means I can use it for future use, if the time ever comes to connect some ev3s to ev3s, 
or pc to ev3s.
