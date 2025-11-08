# ===============================
# Makefile for Half-Duplex Chat App
# ===============================

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
JAVA = java

# Target Java version for compatibility
RELEASE = 11

# ===============================
# Default target
# ===============================
all: $(JAR_FILE)

# Ensure bin directory exists
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# ===============================
# Compile Java source files
# ===============================
compile: $(BIN_DIR)
	@echo "Compiling Java sources (target release $(RELEASE))..."
	$(JAVAC) --release $(RELEASE) -d $(BIN_DIR) $(CORE_SRC)/*.java $(APP_SRC)/*.java

# ===============================
# Create manifest file
# ===============================
$(MANIFEST): | $(BIN_DIR)
	@echo "Creating manifest..."
	@echo "Main-Class: app.ChatApp" > $(MANIFEST)
	@echo "" >> $(MANIFEST)

# ===============================
# Build JAR package
# ===============================
$(JAR_FILE): compile $(MANIFEST)
	@echo "Packaging JAR..."
	cd $(BIN_DIR) && $(JAR) cfm ../$(JAR_FILE) manifest.txt app core
	@echo "Build complete: $(JAR_FILE)"

# ===============================
# Run options
# ===============================
run:
	$(JAVA) -jar $(JAR_FILE)

server:
	$(JAVA) -cp $(BIN_DIR) app.ChatApp -s Liam_Server

client:
	$(JAVA) -cp $(BIN_DIR) app.ChatApp -c Liam_Client 127.0.0.1

# ===============================
# Cleanup
# ===============================
clean:
	rm -rf $(BIN_DIR) $(JAR_FILE)
	@echo "Cleaned build artifacts."

# ===============================
# Help
# ===============================
help:
	@echo "Usage:"
	@echo "  make              -> Build the JAR"
	@echo "  make run          -> Run the application"
	@echo "  make server       -> Run in server mode"
	@echo "  make client       -> Run in client mode"
	@echo "  make clean        -> Remove compiled files"
	@echo "  make help         -> Show this help message"
