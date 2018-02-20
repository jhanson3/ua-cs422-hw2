JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = src/Jacobi.java

all: classes

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class