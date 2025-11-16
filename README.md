# 🗨️ LAN Chat Application

## 📘 Overview

A console-based Java application that enables two devices on the same LAN to exchange messages in real time.
Supports direct IP connections, message logging, and a menu-driven interface for session management and username customization. Designed for simplicity and reliability in controlled LAN environments.

---

## 🧩 Features

* Direct LAN communication between two devices
* Two-way messaging
* Automatic loading of recent message history
* Username customization
* Connection retry options

---

## 🖥️ Usage

### **Start Server**

* Listens for incoming connections on a predefined port.
* Displays incoming messages and allows sending replies.

### **Connect as Client**

* Prompts for the server’s IP address.
* Initiates connection and allows two-way messaging.

### **Change Username**

* Updates the current username.
* Displayed to peers during chat sessions.

### **Exit**

* Closes the application cleanly.

---

## ⚙️ Requirements

* Java 8 or higher
* LAN connection between devices

---

## 📚 References

* [Java Socket Programming – GeeksforGeeks](https://www.geeksforgeeks.org/java/socket-programming-in-java/)
* [Java Date Class – W3Schools](https://www.w3schools.com/java/java_date.asp)

---
