package eu.project.rapid.compiler;

/**
 */
public class RemoteableCodeGenerator {
    /**
     * @param args the command line arguments - first the source code file,
     *             second - template
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out
                    .println("1st parameter input file, 2nd (optional) - output, otherwise stdout");
        } else {
            String input = args[0]; // get the file´s name
            System.out.println("The file is " + input);
            String output;
            try {
                output = args[1]; // get the template´s name
                System.out.println("The output is " + output);
            } catch (ArrayIndexOutOfBoundsException e) {
                //Will use stdout
                output = null;
            }
            // parse
            CodeGenerator generator = new CodeGenerator();
            generator.generate(input, output);
            System.out.println();
        }

    }
}