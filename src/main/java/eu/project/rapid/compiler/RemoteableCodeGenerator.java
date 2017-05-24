package eu.project.rapid.compiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * This is the main class, which gets as a command line argument the file containing the Remoteable
 * class and produces the modified class.
 */
public class RemoteableCodeGenerator {

    private RemoteableCodeGenerator(String projectFolderName) {
        CodeGenerator generator = new CodeGenerator();

        System.out.println("The project is " + projectFolderName);

        Path projectFolder = Paths.get(projectFolderName);
        try (Stream<Path> stream = Files.walk(projectFolder)) {
            stream.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        // Parse the file and extract the QoS parameters
                        generator.handleQosParams(path);

                        // Parse the file and create the new code with offloading possibilities
                        String newCode = generator.generateRemoteMethods(path);
                        if (newCode != null) {
                            backupAndModifyFile(path, newCode);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backupAndModifyFile(Path originalFile, String newCode) {
        try {
            Files.move(originalFile, originalFile.resolveSibling(originalFile + ".back"));
            FileWriter generatedCode = new FileWriter(originalFile.toString());
            try (BufferedWriter out = new BufferedWriter(generatedCode)) {
                out.write(newCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments -
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
        } else {
            new RemoteableCodeGenerator(args[0]);
        }
    }

    private static void usage() {
        System.out.println("1st parameter input file, 2nd (optional) - output, otherwise stdout");
    }
}
