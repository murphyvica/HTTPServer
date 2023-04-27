import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class HTTPServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket sock = new ServerSocket(80)) {
        System.out.print("Listening...");
        while (true) {
            Socket s = sock.accept();
            System.out.println("Connected...");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            OutputStream out = s.getOutputStream();
            String line = in.readLine();
            System.out.println("Line:");
            System.out.println(line);
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken();
            String file = st.nextToken();
            if (command.equals("GET")) {
                System.out.println(file);
                if (file.equals("/")) {
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("\r\n".getBytes());
                } else {
                    if (file.contains(".txt") || file.contains(".html")) {
                        sendFile(file, out);
                        out.write("\r\n".getBytes());
                        out.write("\r\n".getBytes());
                    } else {
                        notImplemented(out);
                    }
                }
                out.flush();
                in.close();
                out.close();

            } else if (command.equals("POST")) {
                while ((line = in.readLine()).length() != 0) {
                    System.out.println(line);
                }
                StringBuilder payload = new StringBuilder();
                while (in.ready()) {
                    payload.append((char) in.read());
                }
                System.out.println("Payload data is: " + payload.toString());
                writeFile(file, payload.toString(), true, out);
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write("\r\n".getBytes());
                out.write("\r\n".getBytes());

                out.flush();
                in.close();
                out.close();

            } else if (command.equals("PUT")) {
                while ((line = in.readLine()).length() != 0) {
                    System.out.println(line);
                }
                StringBuilder payload = new StringBuilder();
                while (in.ready()) {
                    payload.append((char) in.read());
                }
                System.out.println("Payload data is: " + payload.toString());
                newFile(file, out);
                writeFile(file, payload.toString(), false, out);
                out.write("\r\n".getBytes());
                out.write("\r\n".getBytes());

                out.flush();
                in.close();
                out.close();

            } else if (command.equals("DELETE")) {
                System.out.println(file + "For deletion...");
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write("\r\n".getBytes());
                out.write("\r\n".getBytes());
                deleteFile(file);
                }

                out.flush();
                in.close();
                out.close();
            }
            }
        }
    public static void sendFile(String name, OutputStream out) {
        try {
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            File f = new File(s + "/" + name);
            BufferedReader file = new BufferedReader(new FileReader(f));
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            if (name.contains(".txt")) {
                out.write("Content-Type: text/txt".getBytes());
                out.write("\r\n".getBytes());
            }
            if (name.contains(".html")) {
                out.write("Content-Type: text/html".getBytes());
                out.write("\r\n".getBytes());
            }
            out.write("\r\n".getBytes());
            out.write("\r\n".getBytes());
            String line = "";
            while ((line = file.readLine()) != null)
                out.write(line.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
            notFound(out);
        }
    }
    public static void writeFile(String name, String data, Boolean append, OutputStream out) {
        try {
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            File f = new File(s + "/" + name);
            FileWriter myWriter = new FileWriter(f, append);
            myWriter.write(data);
            myWriter.close();
            System.out.print("Successfully wrote to file");
        } catch (Exception ex) {
            ex.printStackTrace();
            notFound(out);
        }
    }
    public static void newFile(String name, OutputStream out) {
        try {
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            File f = new File(s + "/" + name);
            if (f.createNewFile()) {
                System.out.println("File created: " + f.getName());
                out.write("HTTP/1.1 201 Created\r\n".getBytes());
                out.write("\r\n".getBytes());
            } else {
                System.out.println("File already exists, overwriting.");
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write("\r\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String name) {
        String s = Paths.get(".").toAbsolutePath().normalize().toString();
        File f = new File(s + "/" + name);
        if (f.delete()) {
            System.out.println("Deleted the file: " + f.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }

    public static void notFound(OutputStream out) {
        try {
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            String path = Paths.get(".").toAbsolutePath().normalize().toString();
            File f = new File(s + "/" + "404.html");
            BufferedReader file = new BufferedReader(new FileReader(f));
            out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            out.write("Content-Type: text/html".getBytes());
            out.write("\r\n".getBytes());
            out.write("\r\n".getBytes());
            String line = "";
            while ((line = file.readLine()) != null)
                out.write(line.getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void notImplemented(OutputStream out) {
        try {
            String s = Paths.get(".").toAbsolutePath().normalize().toString();
            String path = Paths.get(".").toAbsolutePath().normalize().toString();
            File f = new File(s + "/" + "501.html");
            BufferedReader file = new BufferedReader(new FileReader(f));
            out.write("HTTP/1.1 501 Not Implemented\r\n".getBytes());
            out.write("Content-Type: text/html".getBytes());
            out.write("\r\n".getBytes());
            out.write("\r\n".getBytes());
            String line = "";
            while ((line = file.readLine()) != null)
                out.write(line.getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
