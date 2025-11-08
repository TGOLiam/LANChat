# Makefile for Chat Application

# Directories
SRC_DIR = src
BIN_DIR = bin
CORE_SRC = $(SRC_DIR)/core
APP_SRC = $(SRC_DIR)/app
JAR_FILE = Chat_App.jar
MANIFEST = $(BIN_DIR)/manifest.txt

# Java tools
JAVAC = javac
JAR = jar

# Default target
all: $(JAR_FILE)

# Create bin directory if not existing
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# Compile Java sources into bin/
compile: $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(CORE_SRC)/*.java $(APP_SRC)/*.java

# Create manifest specifying the main entry point
$(MANIFEST): | $(BIN_DIR)
	echo "Main-Class: app.ChatApp" > $(MANIFEST)
	echo "" >> $(MANIFEST)

# Package compiled files into a runnable JAR
$(JAR_FILE): compile $(MANIFEST)
	cd $(BIN_DIR) && $(JAR) cfm ../$(JAR_FILE) manifest.txt app core

# Remove compiled files and jar
clean:
	rm -rf $(BIN_DIR) $(JAR_FILE)

# Run targets (examples)
server:
	java -cp $(BIN_DIR) app.ChatAppT -s Liam_Server

client:
	java -cp $(BIN_DIR) app.ChatAppT -c Liam_Client 127.0.0.1

# Bui
