# Java Intermediate

Use JDK 17 or newer. The first four folders cover strings, recursion, and collections. From the repository root, `java 03_javaIntermediate\Main.java` runs the factorial exercise and prints `120`.

Lessons 38–58 build up a Swing chat running only on the same computer. Each uses the shared `ChatWindow.java` at the root. To try the completed pair, start these from the root in **two PowerShell windows**, server first:

```powershell
javac -d out-server ChatWindow.java 48_javaIntermediate\Server.java
java -cp out-server Server
```
```powershell
javac -d out-client ChatWindow.java 58_javaIntermediate\Client.java
java -cp out-client Client
```

Enter a line and press Enter; the other window displays it. Send `/quit` to disconnect. A graphical desktop and free loopback port 6789 are required. Earlier chapters intentionally stop before full messaging; compile them separately rather than mixing lesson versions. No external files or servers are needed.

Validation: All 25 lesson folders compiled independently. Lesson 03 ran and printed 120; the interactive chat was not exercised.
