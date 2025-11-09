# 🗨️ Half-Duplex LAN Chat Application

## 📘 Overview
A console-based Java application that demonstrates **half-duplex communication** between two devices over a LAN.  
In this model, both sides can send and receive messages, but **only one side communicates at a time**.  
The project showcases basic **socket programming**, **message logging**, and **chat history management**.

---

## 🧩 Features
- Half-duplex message exchange between Server and Client  
- Console-based interface  
- Message logging and history (loads up to 10 latest messages)  
- Beep notifications for new messages  
- Retry and reconnection options  
- Username customization  

---

## 🧱 Class Overview

### **Message**
Stores individual chat message details:
- Username  
- Message content  
- Date sent  

### **Chat (Abstract Class)**
Base class for both Client and Server, responsible for:
- Managing message history  
- Handling socket I/O streams  
- Sending and receiving messages  
- Logging messages  
- Generating timestamps  

### **Client**
Handles the client-side connection.  
- Connects to server via IP address and port  
- Sends user input  
- Waits and receives replies from the server  

### **Server**
Handles the server-side session.  
- Waits for client connection  
- Receives client messages  
- Displays and sends responses  

### **ChatApp**
Menu-driven console interface that allows:
- Starting as Server  
- Connecting as Client  
- Editing username  
- Retrying connections  

---

## ⚙️ Technologies Used
- **Java Sockets** (`Socket`, `ServerSocket`)  
- **I/O Streams** (`DataInputStream`, `DataOutputStream`, `BufferedInputStream`)  
- **File Handling** (`FileReader`, `FileWriter`)  
- **Date and Time** (`java.util.Date`)  
- **Sound Notification** (`Toolkit.getDefaultToolkit().beep()`)

---

## 📚 References
- [Socket Programming in Java – GeeksforGeeks](https://www.geeksforgeeks.org/java/socket-programming-in-java/)  
- [Java Date Class – W3Schools](https://www.w3schools.com/java/java_date.asp)  
- [Make a Beep Sound in Java – Programmer Abroad](https://programmerabroad.com/make-a-beep-sound-in-java/)

---
