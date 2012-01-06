JAVA_HOME=/usr/lib/jvm/java
CLASSPATH=$(JAVA_HOME)/jre/lib/rt.jar:./jcommon-0.5.2.jar:./jfreechart-0.7.0.jar:.
BINDIR=$(JAVA_HOME)/bin

NAME = MarkovApplet
VERSION = 0.021

all: MarkovApplet

.PHONY: package clean

package:
	mkdir $(NAME)-$(VERSION)/
	cp -f *.java jcommon*jar jfreechart*jar CHANGES LICENSE* Makefile *.rst $(NAME)-$(VERSION)/
	tar zcvf $(NAME)-$(VERSION).tar.gz $(NAME)-$(VERSION)
	rm -rf $(NAME)-$(VERSION)/

MarkovApplet: MarkovApplet.java
	$(BINDIR)/javac -classpath $(CLASSPATH) $<

jar: MarkovApplet
	$(BINDIR)/jar cf markovapplet.jar *.class

clean:
	rm -rf *.class markovapplet.jar *.tar.gz
