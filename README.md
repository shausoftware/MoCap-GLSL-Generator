# MoCap-GLSL-Generator
Motion Capture GLSL Generator

This is a tool for generating GLSL shader code from well-formatted C3D & TRC motion capture files. Import motion capture
data to view, edit and create loops. Loops can then be used to generate compact GLSL code suitable for use in 
WebGL applications such as Shadertoy (data is compressed using Fourier transforms and data encoding).  

![mocap-screenshot](mocap-screenshot.png)

This tool was created using Java 11, Spring Boot and React and can be run using the following:

*./gradlew :bootRun*

once running open your browser and goto http://localhost:8080

A Dockerfile has also been included allowing containerisation. 





