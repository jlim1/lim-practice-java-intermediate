import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

final class ChatWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JTextArea history = new JTextArea();
    private final JTextField entry = new JTextField();
    private transient volatile PrintWriter output;

    private ChatWindow(String title) {
        super(title);
        history.setEditable(false);
        entry.setEnabled(false);
        add(new JScrollPane(history), BorderLayout.CENTER);
        add(entry, BorderLayout.SOUTH);
        entry.addActionListener(event -> {
            PrintWriter writer = output;
            if (writer != null) {
                String text = entry.getText();
                writer.println(text);
                append("You: " + text);
                entry.setText("");
            }
        });
        setSize(440, 240);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void append(String text) {
        // Swing components receive updates on the event dispatch thread.
        SwingUtilities.invokeLater(() -> history.append(text + "\n"));
    }

    static void open(boolean server, int lesson) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow(server ? "Chat server" : "Chat client");
            window.setVisible(true);
            int stage = server ? lesson - 38 : lesson - 49;
            if (stage < 2) {
                window.append(stage == 0 ? "Chat display ready." : "Message field ready.");
                return;
            }
            new Thread(() -> window.connect(server, stage), "chat-connection").start();
        });
    }

    private void connect(boolean server, int stage) {
        try {
            InetAddress host = InetAddress.getLoopbackAddress();
            if (stage == 2) {
                append(server ? "Ready to accept a connection." : "Ready to connect to localhost.");
                return;
            }
            if (server) {
                // The listener accepts a peer on this computer and closes after the conversation.
                try (ServerSocket listener = new ServerSocket(6789, 1, host)) {
                    append("Waiting for a local client on port 6789.");
                    try (Socket peer = listener.accept()) {
                        converse(peer, stage, true);
                    }
                }
            } else {
                try (Socket peer = new Socket(host, 6789)) {
                    converse(peer, stage, false);
                }
            }
        } catch (IOException failure) {
            append("Connection ended: " + failure.getMessage());
        }
    }

    private void converse(Socket peer, int stage, boolean server) throws IOException {
        append("Connected to " + peer.getInetAddress().getHostAddress());
        if (stage < 4)
            return;
        // UTF-8 lines give each message a boundary on the byte stream.
        try (PrintWriter writer =
                 new PrintWriter(peer.getOutputStream(), true, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(peer.getInputStream(), StandardCharsets.UTF_8))) {
            writer.println(server ? "Server connected." : "Client connected.");
            if (stage < 5)
                return;
            output = writer;
            if (stage >= 7)
                SwingUtilities.invokeLater(() -> entry.setEnabled(true));
            String line;
            while ((line = reader.readLine()) != null) {
                append("Peer: " + line);
                if (line.equals("/quit"))
                    break;
            }
        } finally {
            output = null;
            SwingUtilities.invokeLater(() -> entry.setEnabled(false));
            if (stage >= 6)
                append("Conversation closed.");
        }
    }
}
