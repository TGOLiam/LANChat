# ===============================
# Makefile for Half-Duplex Chat App
# ===============================

# Directories
SRC_DIR = src
BIN_DIR = bin
CORE_SRC = $(SRC_DIR)/core
APP_SRC = $(SRC_DIR)/app
RELEASE_DIR = $(BIN_DIR)/release
JAR_FILE = $(RELEASE_DIR)/Chat_App.jar
MANIFEST = $(BIN_DIR)/manifest.txt

# Files to include in release
RELEASE_FILES = exec_app.bat README.md

# Java tools
JAVAC = javac
JAR = jar
JAVA = java
ZIP = zip

# Target Java version
RELEASE = 11

# ===============================
# Default target: build + package + zip
# ===============================
all: $(JAR_FILE) copy_release_files compress

# Ensure bin and release directories exist
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(RELEASE_DIR):
	mkdir -p $(RELEASE_DIR)

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
# Build JAR package into release directory
# ===============================
$(JAR_FILE): compile $(MANIFEST) $(RELEASE_DIR)
	@echo "Packaging JAR into $(RELEASE_DIR)..."
	$(JAR) cfm $(JAR_FILE) $(MANIFEST) -C $(BIN_DIR) .
	@echo "Build complete: $(JAR_FILE)"

# ===============================
# Copy additional release files
# ===============================
copy_release_files: $(RELEASE_DIR)
	@echo "Copying additional release files..."
	cp $(RELEASE_FILES) $(RELEASE_DIR)/
	@echo "Files copied to $(RELEASE_DIR)"

# ===============================
# Compress release folder into ZIP
# ===============================
compress: $(RELEASE_DIR)
	@echo "Compressing release directory..."
	cd $(BIN_DIR) && $(ZIP) -r Chat_App.zip release
	@echo "Release compressed to $(BIN_DIR)/Chat_App.zip"

# ===============================
# Run options
# ===============================
run:
	$(JAVA) -jar $(JAR_FILE)

# ===============================
# Cleanup
# ===============================
clean:
	rm -rf $(BIN_DIR) $(JAR_FILE) $(BIN_DIR)/Chat_App.zip
	@echo "Cleaned build artifacts."

# ===============================
# Help
# ===============================
help:
	@echo "Usage:"
	@echo "  make              -> Build the JAR, copy release files, and compress into ZIP"
	@echo "  make run          -> Run the application"
	@echo "  make clean        -> Remove compiled files and ZIP"
	@echo "  make help         -> Show this help message"
