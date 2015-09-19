# expects one parameter: the destination workspace folder
javac EclipseColors.java
jar cf EclipseColors.jar EclipseColors.class
java -cp EclipseColors.jar EclipseColors $1