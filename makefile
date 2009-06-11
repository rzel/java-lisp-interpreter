all: Interpreter.class
	
Interpreter.class: *.java
	javac *.java

clean: 
	rm -f *.class tags
